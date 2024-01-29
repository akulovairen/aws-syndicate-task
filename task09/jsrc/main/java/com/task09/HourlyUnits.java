package com.task09;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class HourlyUnits {
	private String temperature_2m;
	private String time;

	public String getTemperature_2m() {
		return temperature_2m;
	}

	public void setTemperature_2m(String temperature_2m) {
		this.temperature_2m = temperature_2m;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "HourlyUnits{" +
				"temperature_2m='" + temperature_2m + '\'' +
				", time='" + time + '\'' +
				'}';
	}
}
