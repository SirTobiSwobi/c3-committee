package org.SirTobiSwobi.c3.c3committee.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TCConfiguration {

	private long id;
	private int folds;
	private boolean includeImplicits;
	private double assignmentThreshold;
	private String selectionPolicy;
	private TCAthlete[] athletes;
	

	public TCConfiguration(long id, int folds, boolean includeImplicits, double assignmentThreshold,
			String selectionPolicy, TCAthlete[] athletes) {
		super();
		this.id = id;
		this.folds = folds;
		this.includeImplicits = includeImplicits;
		this.assignmentThreshold = assignmentThreshold;
		this.selectionPolicy = selectionPolicy;
		this.athletes = athletes;
	}



	public TCConfiguration() {
		//Jackson deserialization
	}

	@JsonProperty
	public long getId() {
		return id;
	}

	@JsonProperty
	public int getFolds() {
		return folds;
	}

	@JsonProperty
	public boolean isIncludeImplicits() {
		return includeImplicits;
	}

	@JsonProperty
	public double getAssignmentThreshold() {
		return assignmentThreshold;
	}

	@JsonProperty
	public String getSelectionPolicy() {
		return selectionPolicy;
	}

	@JsonProperty
	public TCAthlete[] getAthletes() {
		return athletes;
	}
	
	
}
