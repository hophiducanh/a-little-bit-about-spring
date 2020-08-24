package com.tellyouiam.alittlebitaboutspring.entity.csvformat;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class TaxCodeExport {
	@CsvBindByName(column = "Name")
	@CsvBindByPosition(position = 0)
	private String name;
	
	@CsvBindByName(column = "Description")
	@CsvBindByPosition(position = 1)
	private String description;
	
	@CsvBindByName(column = "Rate")
	@CsvBindByPosition(position = 2)
	private String rate;
	
	public TaxCodeExport(){}
	
	public TaxCodeExport(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public TaxCodeExport(String name, String description, String rate) {
		this.name = name;
		this.description = description;
		this.rate = rate;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getRate() {
		return rate;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setRate(String rate) {
		this.rate = rate;
	}
}
