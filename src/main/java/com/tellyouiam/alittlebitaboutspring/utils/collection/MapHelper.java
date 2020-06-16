package com.tellyouiam.alittlebitaboutspring.utils.collection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class MapHelper {
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
	
	private static Map<String, Object> putToMap(String key, Object value, Map<String, Object> map) {
		Map<String, Object> immutableMap = new ImmutableMap.Builder<String, Object>()
				.put(key, Optional.ofNullable(value).orElse(EMPTY))
				.build();
		Map<String, Object> immutableMapX = ImmutableMap.<String, Object>builder()
				.put(key, Optional.ofNullable(value).orElse(EMPTY))
				.build();
		//map.put(key, Optional.ofNullable(value).orElse(""));
		return immutableMap;
	}
	
	//https://stackoverflow.com/questions/33300011/is-there-a-convenience-method-to-create-a-predicate-that-tests-if-a-field-equals
	public static <T,R> Predicate<T> isEqual(Function<? super T, ? extends R> f, R value) {
		
		return isNull(value)
				? t -> f.apply(t) == null
				: t -> value.equals(f.apply(t));
	}
	
	public static void main(String[] args) {
		Map<String, Object> nonNullMap = new HashMap<>();
		putToMap("a", null, nonNullMap);
//		nonNullMap.forEach((key, value) -> System.out.println("Key: " + key + " ,Value: " + value));
		
		Map<String, Object> immutableMap = new ImmutableMap.Builder<String, Object>()
				.put("a", Optional.ofNullable(null).orElse(EMPTY))
				.put("b", "b")
				.put("c", "c")
				.build();
		
		//immutableMap.forEach((key, value) -> System.out.println("Key: " + key + " ,Value: " + value));
		
		//Map<String, Object> filterMap = Maps.filterKeys(immutableMap, i -> !Objects.equals(i, "a"));
		Map<String, Object> filterMap = Maps.filterEntries(immutableMap, "a"::equals);
		
		filterMap.forEach((key, value) -> System.out.println("Key: " + key + " ,Value: " + value));
	}
}
