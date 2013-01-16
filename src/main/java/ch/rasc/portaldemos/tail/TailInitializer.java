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

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Room;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

@WebListener
public class TailInitializer implements ServletContextListener {

	private final Pattern accessLogPattern = Pattern.compile(getAccessLogRegex(), Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL);

	public ExecutorService executor;

	private LookupService lookupService;

	private Tailer tailer;

	private final Multiset<LatLng> locations = HashMultiset.create();

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
			Room myRoom = null;
			if (myApp != null) {
				myRoom = myApp.room("tail");
			}

			Matcher accessLogEntryMatcher = accessLogPattern.matcher(line);

			if (!accessLogEntryMatcher.matches()) {
				// System.out.println(line);
				return;
			}

			String ip = accessLogEntryMatcher.group(1);
			if (!"-".equals(ip) && !"127.0.0.1".equals(ip)) {
				Location l = lookupService.getLocation(ip);
				if (l != null) {
					LatLng latLng = new LatLng(l.latitude, l.longitude);
					locations.add(latLng);

					if (myRoom != null) {
						myRoom.send("latlng", latLng);
						myRoom.set("locations", locations);
					}
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