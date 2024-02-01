package com.task10;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.task10.dynamoDbDto.ReservationInfo;
import com.task10.dynamoDbDto.ReservationsListDto;
import com.task10.dynamoDbDto.TablesInfoDto;
import com.task10.dynamoDbDto.TablesListDto;
import com.task10.model.Reservations;
import com.task10.model.Tables;
import com.task10.service.CognitoService;
import com.task10.service.DynamoDbService;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@LambdaHandler(lambdaName = "api_handler",
		roleName = "api_handler-role"
)
@EnvironmentVariables(value = {
		@EnvironmentVariable(key = "target_table", value = "Tables"),
		@EnvironmentVariable(key = "reservations_table", value = "Reservations")
})
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final CognitoService cognitoService;
	private final DynamoDbService dynamoDbService;
	private final Gson gson;

	public ApiHandler() {
		this.cognitoService = new CognitoService();
		this.dynamoDbService = new DynamoDbService();
		this.gson = new Gson();
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
		String path = apiGatewayProxyRequestEvent.getPath();

		if (path.equals("/signup")) {
			String requestBody = apiGatewayProxyRequestEvent.getBody();

			Type type = new TypeToken<Map<String, String>>() {
			}.getType();
			Map<String, String> requestBodyMap = gson.fromJson(requestBody, type);

			String firstName = requestBodyMap.get("firstName");
			String lastName = requestBodyMap.get("lastName");
			String email = requestBodyMap.get("email");
			String password = requestBodyMap.get("password");

			try {
				cognitoService.signUpUser(firstName, lastName, email, password);
				return new APIGatewayProxyResponseEvent().withStatusCode(200);
			} catch (CognitoIdentityProviderException e) {
				return new APIGatewayProxyResponseEvent().withStatusCode(400);
			}
		}

		if (path.equals("/signin")) {
			String requestBody = apiGatewayProxyRequestEvent.getBody();

			Type type = new TypeToken<Map<String, String>>() {
			}.getType();
			Map<String, String> requestBodyMap = gson.fromJson(requestBody, type);

			String email = requestBodyMap.get("email");
			String password = requestBodyMap.get("password");

			String accessToken = cognitoService.signInUser(email, password);

			if (accessToken != null) {
				Map<String, String> responseMap = new HashMap<>();
				responseMap.put("accessToken", accessToken);

				return new APIGatewayProxyResponseEvent()
						.withStatusCode(200)
						.withBody(gson.toJson(responseMap));
			} else {
				return new APIGatewayProxyResponseEvent()
						.withStatusCode(400);
			}
		}

		if (path.equals("/tables")) {
			if (apiGatewayProxyRequestEvent.getHttpMethod().equals("GET")) {
				List<Tables> tables = dynamoDbService.getTables();
				TablesListDto tablesListDto = new TablesListDto(tables);
				return new APIGatewayProxyResponseEvent()
						.withBody(gson.toJson(tablesListDto));
			} else if (apiGatewayProxyRequestEvent.getHttpMethod().equals("POST")) {
				TablesInfoDto tablesInfoDto = gson.fromJson(apiGatewayProxyRequestEvent.getBody(), new TypeToken<TablesInfoDto>() {
				}.getType());
				int tableId = dynamoDbService.createTable(tablesInfoDto);

				return new APIGatewayProxyResponseEvent()
						.withBody(String.format("{\"id\": %s }", tableId));
			}
		}

		if (apiGatewayProxyRequestEvent.getResource().equals("/tables/{tableId}")) {
			int tableId = Integer.parseInt(apiGatewayProxyRequestEvent.getPathParameters().get("tableId"));
			return new APIGatewayProxyResponseEvent()
					.withBody(gson.toJson(dynamoDbService.getTableById(tableId)));
		}

		if (path.equals("/reservations")) {
			if (apiGatewayProxyRequestEvent.getHttpMethod().equals("GET")) {
				List<Reservations> reservations = dynamoDbService.getReservations();
				ReservationsListDto reservationsListDto = new ReservationsListDto();
				reservationsListDto.setReservations(reservations);
				return new APIGatewayProxyResponseEvent()
						.withBody(gson.toJson(reservationsListDto));
			} else if (apiGatewayProxyRequestEvent.getHttpMethod().equals("POST")) {
				ReservationInfo reservationsInfo = gson.fromJson(apiGatewayProxyRequestEvent.getBody(), new TypeToken<ReservationInfo>() {
				}.getType());
				try {
					String reservationId = dynamoDbService.createReservation(reservationsInfo);
					return new APIGatewayProxyResponseEvent()
							.withStatusCode(200)
							.withBody(String.format("{\"reservationId\": %s}", reservationId));
				} catch (Exception e) {
					return new APIGatewayProxyResponseEvent()
							.withStatusCode(400)
							.withBody(e.getMessage());
				}
			}
		}
		return new APIGatewayProxyResponseEvent().withStatusCode(500);
	}
}
