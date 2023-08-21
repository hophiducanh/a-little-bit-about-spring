package com.logbasex.mongodb.concurrency_control.optimistic_locking;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("product_v2")
public class ProductV2 {
	@Id
	private String id;
	private String name;
	private int quantity;
	
	@Version
	private Long version;
}
