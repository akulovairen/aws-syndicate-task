package com.task10.dynamoDbDto;

import com.task10.model.Reservations;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationsListDto {
	private List<Map<String, Object>> reservations;
}
