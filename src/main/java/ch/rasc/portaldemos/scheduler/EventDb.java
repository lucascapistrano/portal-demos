package ch.rasc.portaldemos.scheduler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;

public class EventDb {
	private final static Map<Integer, Event> db = new ConcurrentHashMap<>();

	private final static AtomicInteger lastId = new AtomicInteger();

	static {
		db.put(1, new Event(1, 1, "Assignment 1", new DateTime(2013, 1, 7, 10, 0, 0),
				new DateTime(2013, 1, 7, 11, 0, 0)));
		db.put(2, new Event(2, 2, "Assignment 2", new DateTime(2013, 1, 7, 10, 0, 0),
				new DateTime(2013, 1, 7, 12, 0, 0)));
		db.put(3, new Event(3, 3, "Assignment 3", new DateTime(2013, 1, 7, 13, 0, 0),
				new DateTime(2013, 1, 7, 15, 0, 0)));
		db.put(4, new Event(4, 4, "Assignment 4", new DateTime(2013, 1, 7, 16, 0, 0),
				new DateTime(2013, 1, 7, 18, 0, 0)));
		db.put(5, new Event(5, 5, "Assignment 5", new DateTime(2013, 1, 7, 12, 0, 0),
				new DateTime(2013, 1, 7, 13, 0, 0)));
		db.put(6, new Event(6, 6, "Assignment 6", new DateTime(2013, 1, 7, 14, 0, 0),
				new DateTime(2013, 1, 7, 16, 0, 0)));
		lastId.set(6);
	}

	public static Collection<Event> list() {
		return Collections.unmodifiableCollection(db.values());
	}

	public static void create(Event newEvent) {
		newEvent.setId(lastId.incrementAndGet());
		db.put(newEvent.getId(), newEvent);
	}

	public static Event read(int id) {
		return db.get(id);
	}

	public static void update(Event updatedEvent) {
		db.put(updatedEvent.getId(), updatedEvent);
	}

	public static void delete(List<Integer> ids) {
		for (Integer id : ids) {
			db.remove(id);
		}
	}
}
