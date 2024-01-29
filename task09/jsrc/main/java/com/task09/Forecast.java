package com.task09;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class Forecast {
	private int evaluation;
	private double generationtime_ms;
	private Hourly hourly;
	private HourlyUnits hourlyUnits;
	private double latitude;
	private double longitude;
	private String timezone;
	private String timezone_abbreviation;
	private int utc_offset_seconds;

	public Forecast(int evaluation, int generationtime_ms, Hourly hourly, HourlyUnits hourlyUnits, int latitude, int longutude, String timezone, String timezone_abbreviation, int utc_offset_seconds) {
		this.evaluation = evaluation;
		this.generationtime_ms = generationtime_ms;
		this.hourly = hourly;
		this.hourlyUnits = hourlyUnits;
		this.latitude = latitude;
		this.longitude = longutude;
		this.timezone = timezone;
		this.timezone_abbreviation = timezone_abbreviation;
		this.utc_offset_seconds = utc_offset_seconds;
	}

	public int getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public double getGenerationtime_ms() {
		return generationtime_ms;
	}

	public void setGenerationtime_ms(double generationtime_ms) {
		this.generationtime_ms = generationtime_ms;
	}

	public Hourly getHourly() {
		return hourly;
	}

	public void setHourly(Hourly hourly) {
		this.hourly = hourly;
	}

	public HourlyUnits getHourlyUnits() {
		return hourlyUnits;
	}

	public void setHourlyUnits(HourlyUnits hourlyUnits) {
		this.hourlyUnits = hourlyUnits;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getTimezone_abbreviation() {
		return timezone_abbreviation;
	}

	public void setTimezone_abbreviation(String timezone_abbreviation) {
		this.timezone_abbreviation = timezone_abbreviation;
	}

	public int getUtc_offset_seconds() {
		return utc_offset_seconds;
	}

	public void setUtc_offset_seconds(int utc_offset_seconds) {
		this.utc_offset_seconds = utc_offset_seconds;
	}

	@Override
	public String toString() {
		return "Forecast{" +
				"evaluation=" + evaluation +
				", generationtime_ms=" + generationtime_ms +
				", hourly=" + hourly +
				", hourlyUnits=" + hourlyUnits +
				", latitude=" + latitude +
				", longitude=" + longitude +
				", timezone='" + timezone + '\'' +
				", timezone_abbreviation='" + timezone_abbreviation + '\'' +
				", utc_offset_seconds=" + utc_offset_seconds +
				'}';
	}
}
