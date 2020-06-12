package com.tellyouiam.alittlebitaboutspring.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DataTransferObjectHelper {
	//https://stackoverflow.com/questions/7347442/how-to-convert-java-lang-object-to-arraylist
	public static List<?> convertObjectToList(Object obj) {
		List<?> list = new ArrayList<>();
		if (obj.getClass().isArray()) {
			list = Arrays.asList((Object[]) obj);
		} else if (obj instanceof Collection) {
			list = new ArrayList<>((Collection<?>) obj);
		}
		return list;
	}
}
