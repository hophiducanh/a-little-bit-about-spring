package com.tellyouiam.alittlebitaboutspring.entity.csvformat;

import com.google.common.base.Joiner;
import com.opencsv.bean.CsvBindByName;

import java.util.StringJoiner;

public class TaxCodes {
	@CsvBindByName(column = "Tax Code")
	private String name;
	
	@CsvBindByName(column = "Description")
	private String description;
	
	@CsvBindByName(column = "Rate")
	private Double rate;
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Double getRate() {
		return rate;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setRate(Double rate) {
		this.rate = rate;
	}
	
	//	public static void main(String[] args) {
//		String a = null;
//		String b = "abc";
//		String taxCode = "abn";
//		System.out.println(new StringJoiner(" - ").add(a).add(b));
//		System.out.println(Joiner.on(" - ").skipNulls().join(a, b));
//		System.out.println(a);
//		System.out.println(taxCode.matches("(?i)ABN|VWH"));
//	}
}
