package org.SirTobiSwobi.c3.c3committee.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TCModel {
	private long id;
	private long configurationId;
	private double progress;
	private String trainingLog;
	private TCConfiguration configuration;
	private double[] weights;
	
	public TCModel(){
		//Jackson deserialization
	}

	public TCModel(long id, long configurationId, double progress, String trainingLog, TCConfiguration configuration, double[] weights) {
		super();
		this.id = id;
		this.configurationId = configurationId;
		this.progress = progress;
		this.trainingLog = trainingLog;
		this.configuration = configuration;
		this.weights = weights;
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
		
}
