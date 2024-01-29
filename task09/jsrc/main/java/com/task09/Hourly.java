package com.task09;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import java.util.List;

@DynamoDBDocument
public class Hourly {
	private List<Double> temperature_2m;
	private List<String> time;

	public List<Double> getTemperature_2m() {
		return temperature_2m;
	}

	public void setTemperature_2m(List<Double> temperature_2m) {
		this.temperature_2m = temperature_2m;
	}

	public List<String> getTime() {
		return time;
	}

	public void setTime(List<String> time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Hourly{" +
				"temperature_2m=" + temperature_2m +
				", time=" + time +
				'}';
	}
}
