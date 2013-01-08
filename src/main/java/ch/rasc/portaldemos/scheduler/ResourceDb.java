package ch.rasc.portaldemos.scheduler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceDb {

	private final static Map<Integer, Resource> db = new ConcurrentSkipListMap<>();

	private final static AtomicInteger lastId = new AtomicInteger();

	static {
		db.put(1, new Resource(1, "Mike", "blue"));
		db.put(2, new Resource(2, "Linda", "red"));
		db.put(3, new Resource(3, "Don", "yellow"));
		db.put(4, new Resource(4, "Karen", "black"));
		db.put(5, new Resource(5, "Doug", "green"));
		db.put(6, new Resource(6, "Peter", "lime"));
		lastId.set(6);
	}

	public static Collection<Resource> list() {
		return Collections.unmodifiableCollection(db.values());
	}

	public static void create(Resource newResource) {
		newResource.setId(lastId.incrementAndGet());
		db.put(newResource.getId(), newResource);
	}

	public static Resource read(int id) {
		return db.get(id);
	}

	public static void update(Resource updatedResource) {
		db.put(updatedResource.getId(), updatedResource);
	}

	public static void delete(List<Integer> ids) {
		for (Integer id : ids) {
			db.remove(id);
		}
	}

}
