package org.SirTobiSwobi.c3.c3committee.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TCAthlete {
	private long id;
	private String url;
	private String description;
	public TCAthlete() {
		//Jackson deserialization
	}
	public TCAthlete(long id, String url, String description) {
		this.id = id;
		this.url = url;
		this.description = description;
	}
	
	@JsonProperty
	public long getId() {
		return id;
	}
	
	@JsonProperty
	public String getUrl() {
		return url;
	}
	
	@JsonProperty
	public String getDescription() {
		return description;
	}
	
	
}
