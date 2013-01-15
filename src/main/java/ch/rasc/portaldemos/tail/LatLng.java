package ch.rasc.portaldemos.tail;

import java.util.concurrent.atomic.AtomicLong;

public class LatLng implements Comparable<LatLng> {

	private final static AtomicLong idGen = new AtomicLong();

	private final float lat;

	private final float lng;

	private final long id;

	public LatLng(float lat, float lng) {
		this.lat = lat;
		this.lng = lng;
		this.id = idGen.incrementAndGet();
	}

	public float getLat() {
		return lat;
	}

	public float getLng() {
		return lng;
	}

	@Override
	public int compareTo(LatLng o) {
		return Long.compare(o.id, id);
	}

	@Override
	public String toString() {
		return "LatLng [lat=" + lat + ", lng=" + lng + ", id=" + id + "]";
	}

}
