package com.task10.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "cmtr-804a9f76-Reservations-test")
public class Reservations {
	@DynamoDBHashKey(attributeName = "id")
	private String id;
	private int tableNumber;
	private String clientName;
	private String phoneNumber;
	private String date;
	private String slotTimeStart;
	private String slotTimeEnd;

	@DynamoDBHashKey(attributeName = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@DynamoDBAttribute(attributeName = "tableNumber")
	public int getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	@DynamoDBAttribute(attributeName = "clientName")
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@DynamoDBAttribute(attributeName = "phoneNumber")
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@DynamoDBAttribute(attributeName = "date")
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@DynamoDBAttribute(attributeName = "slotTimeStart")
	public String getSlotTimeStart() {
		return slotTimeStart;
	}

	public void setSlotTimeStart(String slotTimeStart) {
		this.slotTimeStart = slotTimeStart;
	}

	@DynamoDBAttribute(attributeName = "slotTimeEnd")
	public String getSlotTimeEnd() {
		return slotTimeEnd;
	}

	public void setSlotTimeEnd(String slotTimeEnd) {
		this.slotTimeEnd = slotTimeEnd;
	}

	@Override
	public String toString() {
		return "Reservations{" +
				" tableNumber=" + tableNumber +
				", clientName='" + clientName + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", date='" + date + '\'' +
				", slotTimeStart='" + slotTimeStart + '\'' +
				", slotTimeEnd='" + slotTimeEnd + '\'' +
				'}';
	}
}
