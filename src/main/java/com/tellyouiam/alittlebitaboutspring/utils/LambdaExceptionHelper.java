package com.tellyouiam.alittlebitaboutspring.utils;

import java.util.function.Function;

public class LambdaExceptionHelper {
	@FunctionalInterface
	public interface ThrowingFunction<T, R> {
		R apply(T t) throws Exception;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Exception, R> R sneakyThrow(Exception t) throws T {
		throw (T) t;
	}
	
	public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> f) {
		return t -> {
			try {
				return f.apply(t);
			} catch (Exception ex) {
				return LambdaExceptionHelper.sneakyThrow(ex);
			}
		};
	}
}
