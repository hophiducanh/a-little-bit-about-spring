package com.tellyouiam.alittlebitaboutspring.utils;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class MapHelper {
	public static Map<String, Object> putToMap(String key, Object value, Map<String, Object> map) {
		 Map<String, Object> immutableMap = ImmutableMap.<String, Object>builder()
				.put(key, Optional.ofNullable(value).orElse(EMPTY))
				.build();
		
		map.put(key, Optional.ofNullable(value).orElse(""));
		return map;
	}
	public static void main(String[] args) {
		Map<String, Object> nonNullMap = new HashMap<>();
		putToMap("a", null, nonNullMap);
		nonNullMap.forEach((key, value) -> System.out.println("Key: " + key + " ,Value: " + value));
	}
}
