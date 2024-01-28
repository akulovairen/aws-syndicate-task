package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.LambdaUrlConfig;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import org.example.OpenMeteoApi;

@LambdaHandler(lambdaName = "api_handler",
		roleName = "api_handler-role",
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
public class ApiHandler implements RequestHandler<Object, String> {

	public String handleRequest(Object request, Context context) {
		OpenMeteoApi openMeteoApi = new OpenMeteoApi();
		String response = openMeteoApi.getWeatherForecast();
		context.getLogger().log(response);
		return openMeteoApi.getWeatherForecast();
	}
}
