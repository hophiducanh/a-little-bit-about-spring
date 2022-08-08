package com.logbasex.aggregatorservice.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class RestAPIService {
	@Value("${rest.square.service.endpoint}")
	private String restSquareUnaryEndpoint;
	
	public Object getUnaryResponse(int number) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();
		Request request = new Request.Builder()
				.url(restSquareUnaryEndpoint + "/rest/square/unary/" + number)
				.method("GET", null)
				.build();
		Response response = client.newCall(request).execute();
		return Objects.requireNonNull(response.body()).string();
	}
}
