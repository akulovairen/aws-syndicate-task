package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenMeteoApi {
	private static final String OPEN_METEO_API_URL = "https://api.open-meteo.com/v1/forecast";

	public String getWeatherForecast() {
		StringBuilder response = new StringBuilder();

		try {
			String apiUrl = String.format("%s?latitude=50.4375&longitude=30.5&hourly=temperature_2m&timezone=Europe/Kyiv",
					OPEN_METEO_API_URL);
			URL url = new URL(apiUrl);

			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line;
			while((line = reader.readLine()) != null) {
				response.append(line);
			}

			reader.close();
			System.out.println("API Response: " + response);
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}

		return response.toString();
	}
}
