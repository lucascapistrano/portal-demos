package ch.rasc.portaldemos.scheduler;

import org.codehaus.jackson.annotate.JsonProperty;

public class Resource {

	@JsonProperty("Id")
	private int id;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("FavoriteColor")
	private String favoriteColor;

	public Resource() {
		// default constructor
	}

	public Resource(int id, String name, String favoriteColor) {
		this.id = id;
		this.name = name;
		this.favoriteColor = favoriteColor;
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

	public String getFavoriteColor() {
		return favoriteColor;
	}

	public void setFavoriteColor(String favoriteColor) {
		this.favoriteColor = favoriteColor;
	}

}
