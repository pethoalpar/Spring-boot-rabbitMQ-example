package com.pethoalpar.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RabbitMessage {

	private String uuid;
	private String title;
	private Date timestamp;
	private String message;

}
