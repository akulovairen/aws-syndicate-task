package com.task10.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CognitoService {
	private final CognitoIdentityProviderClient cognitoClient;

	public CognitoService() {
		this.cognitoClient = CognitoIdentityProviderClient.builder().region(Region.EU_CENTRAL_1).build();
	}

	public void signUpUser(String firstName, String lastName, String email, String password) {
		try {
			AdminCreateUserRequest createUserRequest = AdminCreateUserRequest.builder()
					.userPoolId(getUserPoolId())
					.username(email)
					.temporaryPassword(password)
					.userAttributes(
							AttributeType.builder()
									.name("custom:firstName").value(firstName)
									.build(),
							AttributeType.builder()
									.name("custom:lastName").value(lastName)
									.build()
					)
					.messageAction(MessageActionType.SUPPRESS)
					.build();

			cognitoClient.adminCreateUser(createUserRequest);

			AdminConfirmSignUpRequest confirmSignUpRequest = AdminConfirmSignUpRequest.builder()
					.userPoolId(getUserPoolId())
					.username(email)
					.build();

			cognitoClient.adminConfirmSignUp(confirmSignUpRequest);

		} catch (CognitoIdentityProviderException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
	}

	public String signInUser(String email, String password) {
		try {
			Map<String, String> authParameters = new HashMap<>();
			authParameters.put("USERNAME", email);
			authParameters.put("PASSWORD", password);

			AdminInitiateAuthRequest initiateAuthRequest = AdminInitiateAuthRequest.builder()
					.authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
					.authParameters(authParameters)
					.clientId(getUserPoolClientId())
					.userPoolId(getUserPoolId())
					.build();

			AdminInitiateAuthResponse authResponse = cognitoClient.adminInitiateAuth(initiateAuthRequest);
			AuthenticationResultType authenticationResult = authResponse.authenticationResult();

			return authenticationResult.idToken();
		} catch (CognitoIdentityProviderException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			return null;
		}
	}

	private String getUserPoolClientId() {
		ListUserPoolClientsRequest userPoolClientsRequest = ListUserPoolClientsRequest.builder().userPoolId(getUserPoolId()).build();

		ListUserPoolClientsResponse userPoolClientsResponse = cognitoClient.listUserPoolClients(userPoolClientsRequest);

		return userPoolClientsResponse.userPoolClients().get(0).clientId();
	}

	private String getUserPoolId() {
		ListUserPoolsRequest listUserPoolsRequest = ListUserPoolsRequest.builder().build();

		ListUserPoolsResponse userPoolsResponse = cognitoClient.listUserPools(listUserPoolsRequest);

		return userPoolsResponse.userPools().get(0).id();
	}

}
