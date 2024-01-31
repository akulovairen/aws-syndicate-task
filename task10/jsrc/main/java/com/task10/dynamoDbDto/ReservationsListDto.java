package com.task10.dynamoDbDto;

import com.task10.model.Reservations;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationsListDto {
	private List<Reservations> reservationsList;
}
