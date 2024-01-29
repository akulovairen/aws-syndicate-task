package com.task09;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


@DynamoDBTable(tableName = "cmtr-804a9f76-Weather-test")
public class LatestWeatherData {
	private String id;
	private Forecast forecast;

	@DynamoDBHashKey(attributeName = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Forecast getForecast() {
		return forecast;
	}

	public void setForecast(Forecast forecast) {
		this.forecast = forecast;
	}

	@Override
	public String toString() {
		return "LatestWeatherData{" +
				"id='" + id + '\'' +
				", forecast=" + forecast +
				'}';
	}
}
