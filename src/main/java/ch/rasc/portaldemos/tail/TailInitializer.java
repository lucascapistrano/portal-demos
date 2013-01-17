package ch.rasc.portaldemos.tail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentFamily;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.joda.time.DateTime;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Room;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

@WebListener
public class TailInitializer implements ServletContextListener {

	private final Pattern accessLogPattern = Pattern.compile(getAccessLogRegex(false), Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL);

	private final Pattern vhostAccessLogPattern = Pattern.compile(getAccessLogRegex(true), Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL);

	private final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

	public ExecutorService executor;

	private LookupService lookupService;

	private List<Tailer> tailers;

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		try {
			lookupService = new LookupService(System.getProperty("TAIL_GEOCITY_DAT"), LookupService.GEOIP_INDEX_CACHE);
			tailers = Lists.newArrayList();

			String logFiles = System.getProperty("TAIL_ACCESS_LOG");
			for (String logFile : Splitter.on(",").trimResults().split(logFiles)) {
				Path p = Paths.get(logFile);
				tailers.add(new Tailer(p.toFile(), new ListenerAdapter()));
			}

			executor = Executors.newFixedThreadPool(tailers.size());
			for (Tailer tailer : tailers) {
				executor.execute(tailer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		for (Tailer tailer : tailers) {
			tailer.stop();
		}
		executor.shutdown();
	}

	private class ListenerAdapter extends TailerListenerAdapter {
		@Override
		public void handle(String line) {

			App myApp = App.find("/tail");
			if (myApp == null) {
				return;
			}
			Room myRoom = myApp.room("tail");

			Matcher matcher = accessLogPattern.matcher(line);
			if (!matcher.matches()) {
				matcher = vhostAccessLogPattern.matcher(line);
			}

			if (!matcher.matches()) {
				// System.out.println(line);
				return;
			}

			String ip = matcher.group(1);
			if (!"-".equals(ip) && !"127.0.0.1".equals(ip)) {
				Location l = lookupService.getLocation(ip);
				if (l != null) {
					Access access = new Access();
					access.setIp(ip);
					access.setDate(DateTime.now().getMillis());
					access.setCity(l.city);
					access.setCountry(l.countryName);

					String userAgent = matcher.group(9);
					UserAgent ua = parser.parse(userAgent);
					if (ua != null && ua.getFamily() != UserAgentFamily.UNKNOWN) {
						String uaString = ua.getName() + " " + ua.getVersionNumber().toVersionString();
						uaString += "; " + ua.getOperatingSystem().getName();
						uaString += "; " + ua.getFamily();
						uaString += "; " + ua.getTypeName();
						uaString += "; " + ua.getProducer();

						access.setMessage(matcher.group(4) + "; " + uaString);
					} else {
						access.setMessage(null);
					}
					access.setLl(new float[] { l.latitude, l.longitude });

					myRoom.send("geoip", access);

				}
			}
		}
	}

	private static String getAccessLogRegex(boolean vhostLog) {

		String regexv = "";
		String regex1;
		if (vhostLog) {
			regexv = "^\\S+ ";
			regex1 = "";
		} else {
			regex1 = "^";
		}

		regex1 += "([\\d.-]+)"; // Client IP
		String regex2 = " (\\S+)"; // -
		String regex3 = " (\\S+)"; // -
		String regex4 = " \\[([\\w:/]+\\s[+\\-]\\d{4})\\]"; // Date
		String regex5 = " \"(.*?)\""; // request method and url
		String regex6 = " (\\d{3})"; // HTTP code
		String regex7 = " (\\d+|.+?)"; // Number of bytes
		String regex8 = " \"([^\"]+|.+?)\""; // Referer
		String regex9 = " \"([^\"]+|.+?)\""; // Agent

		return regexv + regex1 + regex2 + regex3 + regex4 + regex5 + regex6 + regex7 + regex8 + regex9;
	}

}