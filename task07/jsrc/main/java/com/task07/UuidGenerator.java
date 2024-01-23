package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@LambdaHandler(lambdaName = "uuid_generator",
	roleName = "uuid_generator-role"
)
@RuleEventSource(targetRule = "uuid_trigger")
@DependsOn(name = "uuid-storage", resourceType = ResourceType.S3_BUCKET)
public class UuidGenerator implements RequestHandler<Object, String> {
	private final String bucket_name = "cmtr-804a9f76-uuid-storage-test";

	@Override
	public String handleRequest(Object input, Context context) {
		List<String> uuids = generateUUIDs();
		String fileName = Instant.now().toString();
		String fileContent = String.format("{\"ids\": %s}", toJsonArrayConvert(uuids));

		S3Client s3Client = S3Client.builder().region(Region.EU_CENTRAL_1).build();

		s3Client.putObject(PutObjectRequest.builder()
						.bucket(bucket_name)
						.key(fileName)
				.build(),
				RequestBody.fromString(fileContent));

		return "UUIDs generated and stored successfully!";
	}

	private List<String> generateUUIDs() {
		return Stream.generate(() -> UUID.randomUUID().toString())
				.limit(10)
				.collect(Collectors.toList());
	}

	private String toJsonArrayConvert(List<String> array){
		return array.stream()
				.map(value -> "\"" + value + "\"")
				.collect(Collectors.joining("," , "[" , "]"));
	}
}
