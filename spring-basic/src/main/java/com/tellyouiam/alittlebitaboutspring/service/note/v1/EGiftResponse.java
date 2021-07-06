package com.tellyouiam.alittlebitaboutspring.service.note.v1;

import com.fasterxml.jackson.annotation.JsonProperty;

class EGiftResponse {
	@JsonProperty("StatusCode")
	private Integer StatusCode;
	@JsonProperty("GiftID")
	private Integer GiftID;
	@JsonProperty("Message")
	private String Message;
	
	
//			@JsonFormat(pattern = "yyyy/MM/dd")
//			@JsonDeserialize(using = LocalDateDeserializer.class)
//			@JsonSerialize(using = LocalDateSerializer.class)
	@JsonProperty("Expiry")
	private String Expiry;
	
	public EGiftResponse() {
	
	}
	
	public Integer getStatusCode() {
		return StatusCode;
	}
	
	public void setStatusCode(Integer statusCode) {
		StatusCode = statusCode;
	}
	
	public Integer getGiftID() {
		return GiftID;
	}
	
	public void setGiftID(Integer giftID) {
		GiftID = giftID;
	}
	
			 public String getMessage() {
				 return Message;
			 }

			 public void setMessage(String message) {
				 Message = message;
			 }
//
//			 public String getExpiry() {
//				 return Expiry;
//			 }
//
//			 public void setExpiry(String expiry) {
//				 Expiry = expiry;
//			 }


//			 public Object getExpiry() {
//				 return Expiry;
//			 }
//
//			 public void setExpiry(Object expiry) {
//				 Expiry = expiry;
//			 }
	
	
			 public String getExpiry() {
				 return Expiry;
			 }

			 public void setExpiry(String expiry) {
				 Expiry = expiry;
			 }
}
