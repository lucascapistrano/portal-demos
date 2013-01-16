package ch.rasc.portaldemos.tail;

import java.util.List;

import com.github.flowersinthesand.portal.Handler;
import com.github.flowersinthesand.portal.Name;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

@Handler("/tail")
public class TailHandler {

	@Name("tail")
	Room room;

	@On.open
	public void open(Socket socket) {
		room.add(socket);

		Multiset<LatLng> locations = (Multiset<LatLng>) room.get("locations");
		if (locations != null) {

			List<ImmutableMap<String, ?>> data = Lists.newArrayList();
			int max = 0;
			for (LatLng latLng : locations.elementSet()) {
				int count = locations.count(latLng);
				if (count > max) {
					max = count;
				}
				data.add(ImmutableMap.of("lat", latLng.getLat(), "lng", latLng.getLng(), "count", count));
			}

			socket.send("latlng", ImmutableMap.of("max", max, "data", data));
		}
	}

}
