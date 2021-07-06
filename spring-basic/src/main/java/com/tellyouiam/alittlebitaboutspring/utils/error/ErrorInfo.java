package com.tellyouiam.alittlebitaboutspring.utils.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ErrorInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(ErrorInfo.class);
	
	public static Properties properties;
	
	static {
		try {
			properties = new Properties();
			InputStream is = ErrorInfo.class.getResourceAsStream("/error_info.properties");
			properties.load(is);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	private static final int UNKNOWN_ERROR_CODE = 1001;
	private static final int CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_CODE= 1002;
	
	private int code;
	private List<String> messages = new ArrayList<>();
	
	public static ErrorInfo newInstance(int code, String message) {
		return new ErrorInfo(code, message);
	}
	
	public ErrorInfo(int code, String... messages) {
		this.code = code;
		this.messages.addAll(Arrays.asList(messages));
	}
	
	public ErrorInfo(String... messages) {
		this.code = UNKNOWN_ERROR_CODE;
		Collections.addAll(this.messages, messages);
	}
	
	public int getCode() {
		return code;
	}
	
	public String getMessage() {
		return messages.size() > 0 ? messages.get(0) : null;
	}
	
	public static final ErrorInfo INTERNAL_SERVER_ERROR = new ErrorInfo(UNKNOWN_ERROR_CODE,
			properties.getProperty("server.error"));
	
	public static final ErrorInfo CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_ERROR = new ErrorInfo(CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_CODE,
			properties.getProperty("cannot.format.ownership.file.using.regex.error"));
}
