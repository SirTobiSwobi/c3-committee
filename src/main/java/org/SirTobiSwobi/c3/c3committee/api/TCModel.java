package org.SirTobiSwobi.c3.c3committee.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TCModel {
	private long id;
	private long configurationId;
	private double progress;
	private String trainingLog;
	private TCConfiguration configuration;
	private double[] weights;
	private int trainingSetSize;
	
	public TCModel(){
		//Jackson deserialization
	}

	public TCModel(long id, long configurationId, double progress, String trainingLog, TCConfiguration configuration, double[] weights, int trainingSetSize) {
		super();
		this.id = id;
		this.configurationId = configurationId;
		this.progress = progress;
		this.trainingLog = trainingLog;
		this.configuration = configuration;
		this.weights = weights;
		this.trainingSetSize=trainingSetSize;
	}
	
	@JsonProperty
	public long getId() {
		return id;
	}

	@JsonProperty
	public long getConfigurationId() {
		return configurationId;
	}

	@JsonProperty
	public double getProgress() {
		return progress;
	}

	@JsonProperty
	public String getTrainingLog() {
		return trainingLog;
	}

	@JsonProperty
	public TCConfiguration getConfiguration() {
		return configuration;
	}

	@JsonProperty
	public double[] getWeights() {
		return weights;
	}

	@JsonProperty
	public int getTrainingSetSize() {
		return trainingSetSize;
	}

	@JsonProperty
	public void setTrainingSetSize(int trainingSetSize) {
		this.trainingSetSize = trainingSetSize;
	}
	
	
		
}
