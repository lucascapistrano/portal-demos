package ch.rasc.portaldemos.scheduler;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;

@JsonIgnoreProperties({ "Cls", "Draggable", "Resizable" })
public class Event {
	@JsonProperty("Id")
	private int id;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("StartDate")
	private DateTime startDate;

	@JsonProperty("EndDate")
	private DateTime endDate;

	@JsonProperty("ResourceId")
	private int resourceId;

	// private final Map<String, String> otherProperties = new HashMap<>();

	public Event() {
		// default constructor
	}

	public Event(int id, int resourceId, String name, DateTime startDate, DateTime endDate) {
		this.id = id;
		this.resourceId = resourceId;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonSerialize(using = ISO8601DateTimeSerializer.class)
	public DateTime getStartDate() {
		return startDate;
	}

	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	@JsonSerialize(using = ISO8601DateTimeSerializer.class)
	public DateTime getEndDate() {
		return endDate;
	}

	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	// @JsonAnyGetter
	// public Map<String, String> getOtherProperties() {
	// return otherProperties;
	// }
	//
	// @JsonAnySetter
	// public void setOtherProperties(String name, String value) {
	// otherProperties.put(name, value);
	// }
}
