package org.SirTobiSwobi.c3.c3committee.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TCModels {
	private TCModel[] models;
	
	public TCModels(){
		//Jackson deserialization
	}

	public TCModels(TCModel[] models) {
		super();
		this.models = models;
	}

	@JsonProperty
	public TCModel[] getModels() {
		return models;
	}
	
	
}
