package org.SirTobiSwobi.c3.c3committee.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TCHash {
	
	
	private String endPoint;
	private String hash;
	
	public TCHash(){
		//Jackson deserialization
	}

	public TCHash(String endPoint, String hash) {
		super();
		this.endPoint = endPoint;
		this.hash = hash;
	}

	@JsonProperty
	public String getEndPoint() {
		return endPoint;
	}

	@JsonProperty
	public String getHash() {
		return hash;
	}
	
	

}
