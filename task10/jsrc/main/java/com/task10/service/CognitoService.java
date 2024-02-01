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
		AttributeType userAttrs = AttributeType.builder()
				.name("email").value(email)
				.build();
		AttributeType userAttrs2 = AttributeType.builder()
				.name("name").value(firstName)
				.build();
		AttributeType userAttrs3 = AttributeType.builder()
				.name("family_name").value(lastName)
				.build();

		List<AttributeType> userAttrsList = new ArrayList<>();
		userAttrsList.add(userAttrs);
		userAttrsList.add(userAttrs2);
		userAttrsList.add(userAttrs3);

		SignUpRequest signUpRequest = SignUpRequest.builder()
				.userAttributes(userAttrsList)
				.username(email)
				.clientId(getUserPoolClientId())
				.password(password)
				.build();

		cognitoClient.signUp(signUpRequest);

		verifyEmail(email);

		AdminConfirmSignUpRequest confirmSignUpRequest = AdminConfirmSignUpRequest.builder()
				.userPoolId(getUserPoolId())
				.username(email)
				.build();

		cognitoClient.adminConfirmSignUp(confirmSignUpRequest);

	}

	private void verifyEmail(String email) {
		AdminUpdateUserAttributesRequest updateAttributesRequest = AdminUpdateUserAttributesRequest.builder()
				.userPoolId(getUserPoolId())
				.username(email)
				.userAttributes(AttributeType.builder().name("email_verified").value("true").build())
				.build();

		cognitoClient.adminUpdateUserAttributes(updateAttributesRequest);
		System.out.println("Email verification successful.");
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
