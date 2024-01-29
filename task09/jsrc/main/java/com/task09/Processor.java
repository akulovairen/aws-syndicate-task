package com.task09;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.syndicate.deployment.annotations.LambdaUrlConfig;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.TracingMode;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import org.example.OpenMeteoApi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(lambdaName = "processor",
		roleName = "processor-role",
		tracingMode = TracingMode.Active,
		layers = {"sdk-layer"},
		runtime = DeploymentRuntime.JAVA8
)
@LambdaLayer(layerName = "sdk-layer",
		libraries = {"lib/open-api-sdk-1.0-SNAPSHOT.jar"},
		runtime = DeploymentRuntime.JAVA8,
		artifactExtension = ArtifactExtension.ZIP)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
@EnvironmentVariables(value = {
		@EnvironmentVariable(key = "target_table", value = "Weather")
})
public class Processor implements RequestHandler<Object, Map<String, Object>> {
	private static final String REGION = "eu-central-1";
	private final DynamoDBMapper dynamoDBMapper;

	public Processor() {
		this.dynamoDBMapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().withRegion(REGION).build());
	}

	public Map<String, Object> handleRequest(Object request, Context context) {
		Gson gson = new Gson();
		OpenMeteoApi openMeteoApi = new OpenMeteoApi();

		Forecast forecast = gson.fromJson(openMeteoApi.getWeatherForecast(), new TypeToken<Forecast>() {
		}.getType());

		LatestWeatherData latestWeatherData = new LatestWeatherData();
		latestWeatherData.setId(UUID.randomUUID().toString());
		latestWeatherData.setForecast(forecast);

		dynamoDBMapper.save(latestWeatherData);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("statusCode", 200);
		resultMap.put("body", latestWeatherData.toString());
		return resultMap;
	}
}
