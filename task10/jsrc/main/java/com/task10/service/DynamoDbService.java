package com.task10.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.task10.dynamoDbDto.ReservationInfo;
import com.task10.dynamoDbDto.TablesInfoDto;
import com.task10.model.Reservations;
import com.task10.model.Tables;

import java.math.BigDecimal;
import java.util.*;

public class DynamoDbService {
	private final AmazonDynamoDB amazonClient;
	private static final String REGION = "eu-central-1";

	private static final String RESERVATION_TABLE = "cmtr-804a9f76-Reservations-test";
	private static final String TABLES_TABLE = "cmtr-804a9f76-Tables-test";


	public DynamoDbService() {
		this.amazonClient = AmazonDynamoDBClientBuilder.standard().withRegion(REGION).build();
	}

	public List<Tables> getTables() {
		DynamoDB dynamoDB = new DynamoDB(amazonClient);
		Table table = dynamoDB.getTable(TABLES_TABLE);

		Iterator<Item> iterator = table.scan().iterator();
		ArrayList<Tables> tablesList = new ArrayList<>();

		while (iterator.hasNext()) {
			Item item = iterator.next();
			Tables tableItem = new Tables();
			tableItem.setId(item.getInt("id"));
			tableItem.setVip((item.getNumber("isVip").equals(BigDecimal.ONE)));
			tableItem.setNumber(item.getInt("number"));
			tableItem.setMinOrder(item.getInt("minOrder"));
			tableItem.setPlaces(item.getInt("places"));
			tablesList.add(tableItem);
		}

		return tablesList;
	}

	public List<Reservations> getReservations() {
		DynamoDB dynamoDB = new DynamoDB(amazonClient);
		Table reservationTable = dynamoDB.getTable(RESERVATION_TABLE);

		DynamoDBMapper mapper = new DynamoDBMapper(amazonClient);

		Map<Class<?>, List<KeyPair>> keyPairMap = new HashMap<>();
		keyPairMap.put(Reservations.class, Collections.emptyList());

		Map<String, List<Object>> result = mapper.batchLoad(keyPairMap);

		List<Object> reservationsObjects = result.get(RESERVATION_TABLE);

		System.out.println(reservationsObjects);


		Iterator<Item> iterator = reservationTable.scan().iterator();
		ArrayList<Reservations> tableList = new ArrayList<>();

		while (iterator.hasNext()) {
			Item item = iterator.next();
			Reservations reservation = new Reservations();
			reservation.setId(item.getString("id"));
			reservation.setDate(item.getString("date"));
			reservation.setClientName(item.getString("clientName"));
			reservation.setSlotTimeEnd(item.getString("slotTimeEnd"));
			reservation.setSlotTimeStart(item.getString("slotTimeStart"));
			reservation.setPhoneNumber(item.getString("phoneNumber"));
			reservation.setTableNumber(item.getInt("tableNumber"));
			tableList.add(reservation);
		}

		return tableList;
	}

	public int createTable(TablesInfoDto tablesInfoDto) {
		Tables tables = new Tables();
		tables.setId(tablesInfoDto.getId());
		tables.setNumber(tablesInfoDto.getNumber());
		tables.setMinOrder(tablesInfoDto.getMinOrder());
		tables.setVip(tablesInfoDto.isVip());
		tables.setPlaces(tablesInfoDto.getPlaces());

		DynamoDBMapper mapper = new DynamoDBMapper(amazonClient);
		mapper.save(tables);

		return tables.getId();
	}

	public String createReservation(ReservationInfo reservationInfo) {
		if (!doesTableExist(reservationInfo.getTableNumber())) {
			System.out.println("Table does not exist");
			throw new RuntimeException("Table does not exist");
		}

		getReservations().stream().filter(x ->
				x.getClientName().equals(reservationInfo.getClientName())
						&& x.getDate().equals(reservationInfo.getDate())
						&& x.getTableNumber() == reservationInfo.getTableNumber()
						&& x.getSlotTimeEnd().equals(reservationInfo.getSlotTimeEnd())
						&& x.getSlotTimeStart().equals(reservationInfo.getSlotTimeStart())
						&& x.getPhoneNumber().equals(reservationInfo.getPhoneNumber())
		).findFirst().ifPresent(x -> {
			System.out.println("Item already exists");
			throw new RuntimeException();
		});

//		if (doesReservationExist(reservationInfo)) {
//			System.out.println("Reservation already exists");
//			throw new RuntimeException("Reservation already exists");
//		}

		Reservations reservations = new Reservations();
		String reservationId = UUID.randomUUID().toString();
		reservations.setId(reservationId);
		reservations.setTableNumber(reservationInfo.getTableNumber());
		reservations.setClientName(reservationInfo.getClientName());
		reservations.setPhoneNumber(reservationInfo.getPhoneNumber());
		reservations.setDate(reservationInfo.getDate());
		reservations.setSlotTimeStart(reservationInfo.getSlotTimeStart());
		reservations.setSlotTimeEnd(reservationInfo.getSlotTimeEnd());

		DynamoDBMapper mapper = new DynamoDBMapper(amazonClient);
		mapper.save(reservations);
		System.out.println("Reservation success");

		return reservationId;
	}

	private boolean doesTableExist(int tableNumber) {
		List<Tables> allTables = getTables();

		for (Tables table : allTables) {
			if (table.getNumber() == tableNumber) {
				return true;
			}
		}
		return false;
	}

	private boolean doesReservationExist(ReservationInfo reservationInfo) {
		List<Reservations> reservationsList = getReservations();

		for (Reservations reservation : reservationsList) {
			if (reservation.getTableNumber() == reservationInfo.getTableNumber()
					&& reservation.getDate().equals(reservationInfo.getDate())
					&& reservation.getClientName().equals(reservationInfo.getClientName())
					&& reservation.getSlotTimeEnd().equals(reservationInfo.getSlotTimeEnd())
					&& reservation.getSlotTimeStart().equals(reservationInfo.getSlotTimeStart())
					&& reservation.getPhoneNumber().equals(reservationInfo.getPhoneNumber())) {
				return true;
			}
		}
		return false;
	}

	public Tables getTableById(int tableId) {
		DynamoDB dynamoDB = new DynamoDB(amazonClient);
		Table table = dynamoDB.getTable(TABLES_TABLE);

		GetItemSpec getItemSpec = new GetItemSpec().withPrimaryKey("id", tableId);
		Item item = table.getItem(getItemSpec);

		Tables tables = new Tables();
		tables.setId(item.getInt("id"));
		tables.setVip((item.getNumber("isVip").equals(BigDecimal.ONE)));
		tables.setNumber(item.getInt("number"));
		tables.setMinOrder(item.getInt("minOrder"));
		tables.setPlaces(item.getInt("places"));
		return tables;
	}
}
