package ch.rasc.portaldemos.scheduler;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;

@JsonIgnoreProperties({ "Cls", "Draggable", "Resizable" })
public class CustomEvent {

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

	// true, false, 'start' or 'end'
	@JsonProperty("Resizable")
	private Object resizable;

	@JsonProperty("Draggable")
	private Boolean draggable;

	@JsonProperty("Cls")
	private String cls;

	@JsonProperty("Blocked")
	private Boolean blocked;

	@JsonProperty("BlockedBy")
	private String blockedBy;

	@JsonProperty("Done")
	private Boolean done;

	public CustomEvent() {
		// default constructor
	}

	public CustomEvent(int id, int resourceId, String name, DateTime startDate, DateTime endDate, boolean done) {
		this.id = id;
		this.resourceId = resourceId;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.done = done;
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

	public Object getResizable() {
		return resizable;
	}

	public void setResizable(Object resizable) {
		this.resizable = resizable;
	}

	public Boolean getDraggable() {
		return draggable;
	}

	public void setDraggable(Boolean draggable) {
		this.draggable = draggable;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public Boolean getBlocked() {
		return blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	public String getBlockedBy() {
		return blockedBy;
	}

	public void setBlockedBy(String blockedBy) {
		this.blockedBy = blockedBy;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	@Override
	public String toString() {
		return "CustomEvent [id=" + id + ", name=" + name + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", resourceId=" + resourceId + ", resizable=" + resizable + ", draggable=" + draggable + ", cls="
				+ cls + ", blocked=" + blocked + ", blockedBy=" + blockedBy + ", done=" + done + "]";
	}

}
