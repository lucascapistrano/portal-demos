package ch.rasc.portaldemos.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.Data;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;
import com.google.common.collect.ImmutableMap;

@Bean
public class SchedulerHandler {

	private final static ObjectMapper mapper = new ObjectMapper();

	@Wire
	Room room;

	@On
	public void open(Socket socket) {
		room.add(socket);
	}

	@On
	public void client_doInitialLoad(Socket socket, @Data Map<String, Object> data) {
		String storeType = (String) data.get("storeType");

		Map<String, Object> result = new HashMap<>();
		if (storeType.equals("resource")) {
			result.put("data", ResourceDb.list());
		} else {
			result.put("data", EventDb.list());
		}
		result.put("storeType", storeType);
		socket.send("server_doInitialLoad", result);
	}

	// Update records in DB and inform other clients about the change
	@On
	public void client_doUpdate(Socket socket, @Data Map<String, Object> msg) {
		String storeType = (String) msg.get("storeType");
		List<Map<String, Object>> records = (List<Map<String, Object>>) msg.get("records");

		for (Map<String, Object> record : records) {
			if (storeType.equals("resource")) {
				Resource res = mapper.convertValue(record, Resource.class);
				ResourceDb.update(res);
			} else {
				Event event = mapper.convertValue(record, Event.class);
				EventDb.update(event);
			}
		}

		room.out(socket).send("server_doUpdate", msg);
	}

	// Add record to DB and inform other clients about the change
	@On
	public void client_doAdd(Socket socket, @Data Map<String, Object> msg) {
		String storeType = (String) msg.get("storeType");
		List<Map<String, Object>> records = (List<Map<String, Object>>) msg.get("records");

		List<Object> updatedRecords = new ArrayList<>();
		List<ImmutableMap<String, ?>> ids = new ArrayList<>();

		for (Map<String, Object> r : records) {
			Map<String, Object> record = (Map<String, Object>) r.get("record");
			String internalId = (String) r.get("internalId");

			if (storeType.equals("resource")) {
				Resource res = mapper.convertValue(record, Resource.class);
				ResourceDb.create(res);
				updatedRecords.add(res);

				ids.add(ImmutableMap.of("internalId", internalId, "id", res.getId()));

			} else {
				Event event = mapper.convertValue(record, Event.class);
				EventDb.create(event);
				updatedRecords.add(event);

				ids.add(ImmutableMap.of("internalId", internalId, "id", event.getId()));
			}
		}

		room.out(socket).send("server_doAdd", ImmutableMap.of("records", updatedRecords, "storeType", storeType));
		socket.send("server_syncId", ImmutableMap.of("ids", ids, "storeType", storeType));
	}

	// Remove record from DB and inform other clients about the change
	@On
	public void client_doRemove(Socket socket, @Data Map<String, Object> msg) {
		String storeType = (String) msg.get("storeType");
		List<Integer> ids = (List<Integer>) msg.get("ids");

		if (storeType.equals("resource")) {
			ResourceDb.delete(ids);
		} else {
			EventDb.delete(ids);
		}

		room.out(socket).send("server_doRemove", ImmutableMap.of("ids", ids, "storeType", storeType));
	}

}
