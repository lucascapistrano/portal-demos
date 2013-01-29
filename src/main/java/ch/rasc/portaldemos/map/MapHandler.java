package ch.rasc.portaldemos.map;

import java.util.concurrent.TimeUnit;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Prepare;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;
import com.google.common.collect.ImmutableList;

@Bean
public class MapHandler {

	@Wire
	Room room;

	@Prepare
	public void prepare() {
		MapInitializer.threadPool.scheduleWithFixedDelay(new Car("driveBlue", Route.routeBlue), 1, 1, TimeUnit.SECONDS);
		MapInitializer.threadPool.scheduleWithFixedDelay(new Car("driveRed", Route.routeRed), 2000, 1200,
				TimeUnit.MILLISECONDS);
	}

	@On
	public void open(Socket socket) {
		room.add(socket);
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
			if (room.size() > 0) {
				room.send(event, route.get(index));
				index++;
				if (index >= route.size()) {
					index = 0;
				}
			}
		}
	}
}
