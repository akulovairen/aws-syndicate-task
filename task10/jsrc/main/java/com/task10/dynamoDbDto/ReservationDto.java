package com.task10.dynamoDbDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
	private int tableNumber;
	private String clientName;
	private String phoneNumber;
	private String date;
	private String slotTimeStart;
	private String slotTimeEnd;
}
