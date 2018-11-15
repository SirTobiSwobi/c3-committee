package org.SirTobiSwobi.c3.c3committee.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TCCategorization {
	private long id;
	private long documentId;
	private long categoryId;
	private double probability;
	
	public TCCategorization(){
		//Jackson deserialization;
	}

	public TCCategorization(long id, long documentId, long categoryId, double probability) {
		super();
		this.id = id;
		this.documentId = documentId;
		this.categoryId = categoryId;
		this.probability = probability;
	}
	@JsonProperty
	public long getId() {
		return id;
	}
	@JsonProperty
	public long getDocumentId() {
		return documentId;
	}
	@JsonProperty
	public long getCategoryId() {
		return categoryId;
	}
	@JsonProperty
	public double getProbability() {
		return probability;
	}
	
	
}
