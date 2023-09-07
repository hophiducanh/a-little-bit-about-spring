package com.logbasex.reactive.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Customer {
	@Id
	private String id;
	private String name;
}
