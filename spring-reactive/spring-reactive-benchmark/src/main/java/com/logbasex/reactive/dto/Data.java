package com.logbasex.reactive.dto;

import lombok.Builder;

@lombok.Data
@Builder
public class Data {
	private Long threadId;
	private Long requestCameTime;
	private String data;
}
