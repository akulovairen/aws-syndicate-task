package com.task06;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.syndicate.deployment.annotations.events.DynamoDbEvents;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import sun.tools.jconsole.Tab;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(lambdaName = "audit_producer",
	roleName = "audit_producer-role"
)
@DynamoDbTriggerEventSource(targetTable = "Configuration" , batchSize = 1)
@DependsOn(name = "Configuration", resourceType = ResourceType.DYNAMODB_TABLE)
public class AuditProducer implements RequestHandler<DynamodbEvent, Void> {
	private static final String INSERT = "INSERT";
	private static final String MODIFY = "MODIFY";
//	private final String DYNAMO_DB_CONFIGURATION_TABLE = "cmtr-804a9f76-Configuration-test";
	private final String DYNAMO_DB_AUDIT_TABLE = "cmtr-804a9f76-Audit-test";
	private final AmazonDynamoDB dynamoDbClient = AmazonDynamoDBClientBuilder.defaultClient();
	private final DynamoDB dynamoDB = new DynamoDB(dynamoDbClient);

	@Override
	public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
		Table auditTable = dynamoDB.getTable(DYNAMO_DB_AUDIT_TABLE);

		for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
			if (INSERT.equals(record.getEventName())) {
				createAuditItem(record, auditTable);
			} else if (MODIFY.equals(record.getEventName())){
				updateAuditItem(record,auditTable);
			}
		}
		return null;
	}

	private void createAuditItem(DynamodbEvent.DynamodbStreamRecord dynamodbStreamRecord, Table auditTable){
		Map<String, AttributeValue> newImage = dynamodbStreamRecord.getDynamodb().getNewImage();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
		String modificationTime = ZonedDateTime.now().format(formatter);

		String key = newImage.get("key").getS();
		String value = newImage.get("value").getS();

		Map<String, String> eventData = new LinkedHashMap<>();
		eventData.put("key", key);
		eventData.put("value", value);

		Item auditItem = new Item()
				.withPrimaryKey("id", UUID.randomUUID().toString())
				.withString("itemKey", key)
				.withString("modificationTime", modificationTime)
				.withMap("newValue", eventData);

		auditTable.putItem(auditItem);
	}

	private void updateAuditItem(DynamodbEvent.DynamodbStreamRecord dynamodbStreamRecord, Table auditTable){
		Map<String, AttributeValue> newImage = dynamodbStreamRecord.getDynamodb().getNewImage();
		Map<String, AttributeValue> oldImage = dynamodbStreamRecord.getDynamodb().getOldImage();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
		String modificationTime = ZonedDateTime.now().format(formatter);

		String oldValue = oldImage.get("value").getS();
		String newValue = newImage.get("value").getS();
		String key = newImage.get("key").getS();

		Item auditItem = new Item()
				.withPrimaryKey("id", UUID.randomUUID().toString())
				.withString("itemKey", key)
				.withString("modificationTime", modificationTime)
				.withString("updatedAttribute", "value")
				.withString("oldValue", oldValue)
				.withString("newValue", newValue);

		auditTable.putItem(auditItem);
	}
}
