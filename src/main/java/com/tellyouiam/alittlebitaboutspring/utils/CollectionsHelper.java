package com.tellyouiam.alittlebitaboutspring.utils;

import java.util.Map;
import java.util.stream.Collectors;

public class CollectionsHelper {
	public static <K, V> Map<K, V> getCommonMap(Map<? extends K, ? extends V> firstMap,
	                                             Map<? extends K, ? extends V> secondMap) {
		return getCommonMap(firstMap, secondMap, true);
	}
	
	public static <K, V> Map<K, V> getCommonMap(Map<? extends K, ? extends V> firstMap,
	                                             Map<? extends K, ? extends V> secondMap, boolean isSameValue) {
		Map<K, V> commonMap;
		commonMap = firstMap.entrySet().stream()
				.filter(x -> secondMap.containsKey(x.getKey()))
				.filter(isSameValue ? y -> secondMap.containsValue(y.getValue()) : y -> true)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		return commonMap;
	}
	
	//polymorphism takes two form : overriding and overloading
	public static <K, V> Map<K, V> getDiffMap(Map<? extends K, ? extends V> firstMap,
	                                             Map<? extends K, ? extends V> secondMap) {
		return getDiffMap(firstMap, secondMap, true);
	}
	
	//get difference with difference key in two maps (isDiffKey = true) or get difference with value difference with the same key in two maps.
	public static <K, V> Map<K, V> getDiffMap(Map<? extends K, ? extends V> firstMap,
	                                           Map<? extends K, ? extends V> secondMap, boolean isDiffKey) {
		Map<K, V> diffMap;
		diffMap = firstMap.entrySet().stream()
				.filter(isDiffKey ? x -> !secondMap.containsKey(x.getKey()) : x -> secondMap.containsKey(x.getKey()))
				.filter(y -> !secondMap.containsValue(y.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		return diffMap;
	}
}
