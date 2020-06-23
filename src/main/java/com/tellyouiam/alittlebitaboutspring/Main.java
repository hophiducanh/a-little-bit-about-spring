package com.tellyouiam.alittlebitaboutspring;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		int x = StringUtils.getLevenshteinDistance("cat", "hat");
		//System.out.println(x);
		
		LevenshteinDistance distance = new LevenshteinDistance();
		String input1 = "5/166 Glen Eira Road ELSTERNWICK 3185 VIC AUSTRALIA";
		String input2 = "5 / 166 Gleneira Road ELSTERNWICK 3185 VIC AUSTRALIA";
		int y = distance.apply(StringUtils.deleteWhitespace(input1.toLowerCase()),
				StringUtils.deleteWhitespace(input2.toLowerCase()));
		
		System.out.println(y);
		
		System.out.println("*********************");
		List<Integer> l = Arrays.asList(1,2,3);
		List<Integer> k = Arrays.asList(1,2,5);
		CollectionUtils.disjunction(l, k).forEach(System.out::println);
	}
}
