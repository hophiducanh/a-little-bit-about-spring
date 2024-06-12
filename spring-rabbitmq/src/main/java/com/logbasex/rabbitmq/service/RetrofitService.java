package com.logbasex.rabbitmq.service;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.log4j.Log4j2;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class RetrofitService {

	public static <S> S createService(Class<S> serviceClass, Interceptor interceptor, String url) {
		return createService(serviceClass, interceptor, url, null, null);
	}
	
	public static <S> S createService(Class<S> serviceClass,
									  Interceptor interceptor,
									  String url,
									  Duration callTimeOut,
									  Duration readTimeOut) {
		
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		
		if (callTimeOut == null) {
			httpClient.callTimeout(60, TimeUnit.SECONDS);
		} else {
			httpClient.callTimeout(callTimeOut);
		}
		
		if (readTimeOut == null) {
			httpClient.readTimeout(60, TimeUnit.SECONDS);
		} else {
			httpClient.readTimeout(readTimeOut);
		}
		
		if (interceptor != null && !httpClient.interceptors().contains(interceptor)) {
			httpClient.addInterceptor(interceptor);
		}
		
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		// set your desired log level
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
		
		if (!httpClient.interceptors().contains(loggingInterceptor)) {
			// add logging as last interceptor
			httpClient.addInterceptor(loggingInterceptor);
		}
		
		ObjectMapper objectMapper = JsonMapper.builder()
				.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.build();
		
		String baseUrl = appendUrlTrailingSlash(url);
		Retrofit.Builder builder = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.addConverterFactory(JacksonConverterFactory.create(objectMapper))
				.client(httpClient.build());
		
		Retrofit retrofit = builder.build();
		
		return retrofit.create(serviceClass);
	}
	
	/**
	 * baseUrl resolution: <a href="https://futurestud.io/tutorials/retrofit-2-url-handling-resolution-and-parsing">...</a>
	 */
	private static String appendUrlTrailingSlash(String url) {
		return url.endsWith("/") ? url : url + "/";
	}
}
