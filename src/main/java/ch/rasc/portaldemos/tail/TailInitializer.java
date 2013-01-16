package ch.rasc.portaldemos.tail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.joda.time.DateTime;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Room;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

@WebListener
public class TailInitializer implements ServletContextListener {

	private final Pattern accessLogPattern = Pattern.compile(getAccessLogRegex(), Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL);

	public ExecutorService executor;

	private LookupService lookupService;

	private Tailer tailer;

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		try {
			lookupService = new LookupService(System.getProperty("TAIL_GEOCITY_DAT"), LookupService.GEOIP_INDEX_CACHE);

			Path p = Paths.get(System.getProperty("TAIL_ACCESS_LOG"));
			tailer = new Tailer(p.toFile(), new ListenerAdapter());

			executor = Executors.newFixedThreadPool(1);
			executor.execute(tailer);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		tailer.stop();
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

			Matcher accessLogEntryMatcher = accessLogPattern.matcher(line);

			if (!accessLogEntryMatcher.matches()) {
				// System.out.println(line);
				return;
			}

			String ip = accessLogEntryMatcher.group(1);
			if (!"-".equals(ip) && !"127.0.0.1".equals(ip)) {
				Location l = lookupService.getLocation(ip);
				if (l != null) {
					Access access = new Access();
					access.setIp(ip);
					access.setDate(DateTime.now().getMillis());
					access.setCity(l.city);
					access.setCountry(l.countryName);
					access.setMessage(line);
					access.setLl(new float[] { l.latitude, l.longitude });

					myRoom.send("geoip", access);

				}
			}
		}
	}

	private static String getAccessLogRegex() {
		String regex1 = "^([\\d.-]+)"; // Client IP
		String regex2 = " (\\S+)"; // -
		String regex3 = " (\\S+)"; // -
		String regex4 = " \\[([\\w:/]+\\s[+\\-]\\d{4})\\]"; // Date
		String regex5 = " \"(.*?)\""; // request method and url
		String regex6 = " (\\d{3})"; // HTTP code
		String regex7 = " (\\d+|(.+?))"; // Number of bytes
		String regex8 = " \"([^\"]+|(.+?))\""; // Referer
		String regex9 = " \"([^\"]+|(.+?))\""; // Agent

		return regex1 + regex2 + regex3 + regex4 + regex5 + regex6 + regex7 + regex8 + regex9;
	}

}