package com.tellyouiam.basic.json;

import lombok.Getter;
import lombok.Setter;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;
import java.util.List;

//https://www.baeldung.com/java-json-binding-api
//@Data
@Getter
@Setter
public class Temp {
	@JsonbProperty("id")
	private String id;
	
	@JsonbProperty("points")
	private List<List<BigDecimal>> points;
	
	public static void main(String[] args) {
		String jsonString = "{\n" +
		                    "  \"id\": \"test\",\n" +
		                    "  \"points\": [\n" +
		                    "    [\n" +
		                    "      -24.787439346313477,\n" +
		                    "      5.5551919937133789\n" +
		                    "    ],\n" +
		                    "    [\n" +
		                    "      -23.788913726806641,\n" +
		                    "      6.7245755195617676\n" +
		                    "    ],\n" +
		                    "    [\n" +
		                    "      -22.257251739501953,\n" +
		                    "      7.2461895942687988\n" +
		                    "    ]\n" +
		                    "  ]\n" +
		                    "}";
		Jsonb jsonb = JsonbBuilder.create();
		Temp temp = jsonb.fromJson(jsonString, Temp.class);
		System.out.println(temp);
	}
}
