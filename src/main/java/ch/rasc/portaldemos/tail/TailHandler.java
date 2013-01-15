package ch.rasc.portaldemos.tail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import com.github.flowersinthesand.portal.Handler;
import com.github.flowersinthesand.portal.Name;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Prepare;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.google.common.collect.MinMaxPriorityQueue;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

@Handler("/tail")
public class TailHandler {

	private final Pattern accessLogPattern = Pattern.compile(getAccessLogRegex(), Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL);

	private LookupService cl;

	@Name("tail")
	Room room;

	@Prepare
	public void prepare() throws IOException {
		Executor executor = Executors.newFixedThreadPool(1);

		cl = new LookupService(System.getProperty("TAIL_GEOCITY_DAT"), LookupService.GEOIP_INDEX_CACHE);

		Path p = Paths.get(System.getProperty("TAIL_ACCESS_LOG"));
		Tailer tailer = new Tailer(p.toFile(), new ListenerAdapter());
		executor.execute(tailer);
	}

	@On.open
	public void open(Socket socket) {
		room.add(socket);

		MinMaxPriorityQueue<LatLng> lastEntries = (MinMaxPriorityQueue<LatLng>) room.get("last_entries");
		if (lastEntries != null) {
			socket.send("latlng", lastEntries);
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

	private class ListenerAdapter extends TailerListenerAdapter {
		@Override
		public void handle(String line) {

			MinMaxPriorityQueue<LatLng> lastEntries = (MinMaxPriorityQueue<LatLng>) room.get("last_entries");
			if (lastEntries == null) {
				lastEntries = MinMaxPriorityQueue.maximumSize(500).create();
				room.set("last_entries", lastEntries);
			}

			Matcher accessLogEntryMatcher = accessLogPattern.matcher(line);

			if (!accessLogEntryMatcher.matches()) {
				System.out.println(line);
				return;
			}

			String ip = accessLogEntryMatcher.group(1);
			if (!"-".equals(ip) && !"127.0.0.1".equals(ip)) {
				Location l = cl.getLocation(ip);
				if (l != null) {
					LatLng latLng = new LatLng(l.latitude, l.longitude);
					lastEntries.add(latLng);
					room.send("latlng", latLng);
				}
			}
		}
	}
}
