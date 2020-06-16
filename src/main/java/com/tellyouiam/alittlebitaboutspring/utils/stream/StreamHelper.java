package com.tellyouiam.alittlebitaboutspring.utils.stream;

import org.apache.poi.ss.formula.functions.T;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : Ho Anh
 * @since : 08/02/2020, Sat
 **/
public class StreamHelper {
	public static <T> Collector<T, ?, T> singletonCollector() {
		return getCollectorFunction(getListTSingleFunction());
	}
	
	
	public static <T> Collector<T, ?, T> firstInListCollector() {
		return getCollectorFunction(getListTFirstFunction());
	}
	
	
	public static <T> Collector<T, ?, T> lastInListCollector() {
		return getCollectorFunction(getLastTFirstFunction());
	}
	
	
	private static <T> Collector<T, ?, T> getCollectorFunction(Function<List<T>, T> listTFunction) {
		return Collectors.collectingAndThen(Collectors.toList(), listTFunction);
	}
	
	
	private static <T> Function<List<T>, T> getListTSingleFunction() {
		return list -> {
			if (list.size() != 1) {
				throw new IllegalStateException();
			}
			return list.get(0);
		};
	}
	
	
	private static <T> Function<List<T>, T> getListTFirstFunction() {
		return list -> {
			if (list.size() == 0) {
				throw new IllegalStateException();
			}
			return list.get(0);
		};
	}
	
	
	private static <T> Function<List<T>, T> getLastTFirstFunction() {
		return list -> {
			if (list.size() == 0) {
				throw new IllegalStateException();
			}
			return list.get(list.size() - 1);
		};
	}
	
	public static <T> long countDistinctCollectionElement(Collection<T> collection) {
		return collection.stream().distinct().count();
	}
	
	public static <T> long countDistinctStreamElement(Stream<T> stream) {
		return stream.distinct().count();
	}
}
