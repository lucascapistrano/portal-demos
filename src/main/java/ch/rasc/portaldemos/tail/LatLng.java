package ch.rasc.portaldemos.tail;

public class LatLng {
	private final float lat;

	private final float lng;

	public LatLng(float lat, float lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public float getLat() {
		return lat;
	}

	public float getLng() {
		return lng;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(lat);
		result = prime * result + Float.floatToIntBits(lng);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		LatLng other = (LatLng) obj;
		if (Float.floatToIntBits(lat) != Float.floatToIntBits(other.lat)) {
			return false;
		}
		if (Float.floatToIntBits(lng) != Float.floatToIntBits(other.lng)) {
			return false;
		}
		return true;
	}

}
