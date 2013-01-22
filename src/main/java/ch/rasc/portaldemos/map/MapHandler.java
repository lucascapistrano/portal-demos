package ch.rasc.portaldemos.map;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.Wire;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.google.common.collect.ImmutableList;

@Bean
public class MapHandler {

	private static ScheduledExecutorService threadPool;

	@Wire("map")
	Room room;

	@On.close
	public void close(Socket socket) {
		System.out.println("closing: " + socket);
		if (room.size() == 0) {
			threadPool.shutdownNow();
			threadPool = null;
		}
	}

	@On.open
	public void open(Socket socket) {
		room.add(socket);
		if (threadPool == null) {
			threadPool = Executors.newScheduledThreadPool(1);
			threadPool.scheduleWithFixedDelay(new Car("driveBlue", Route.routeBlue), 1, 1, TimeUnit.SECONDS);
			threadPool.scheduleWithFixedDelay(new Car("driveRed", Route.routeRed), 2000, 1200, TimeUnit.MILLISECONDS);
		}
	}

	private class Car implements Runnable {

		private int index = 0;

		private final String event;

		private final ImmutableList<LatLng> route;

		public Car(String event, ImmutableList<LatLng> route) {
			this.event = event;
			this.route = route;
		}

		@Override
		public void run() {
			room.send(event, route.get(index));
			index++;
			if (index >= route.size()) {
				index = 0;
			}
		}
	}
}
