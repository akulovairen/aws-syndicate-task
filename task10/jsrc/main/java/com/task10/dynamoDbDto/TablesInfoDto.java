package com.task10.dynamoDbDto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TablesInfoDto {
	private int id;
	private int number;
	private int places;
	private boolean isVip;
	private int minOrder;
}
