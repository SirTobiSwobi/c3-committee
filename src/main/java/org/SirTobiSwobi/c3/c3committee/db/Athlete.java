package org.SirTobiSwobi.c3.c3committee.db;


public class Athlete {
	private long id;
	private String url;
	private String description;
	
	public Athlete(long id, String url, String description) {
		super();
		this.id = id;
		this.url = url;
		this.description = description;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String toString(){
		return id+" "+url;    
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
