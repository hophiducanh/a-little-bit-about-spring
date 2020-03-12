package com.tellyouiam.alittlebitaboutspring.dto.csvformat;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;

@Getter
@Setter
public class OpeningBalance {
	@CsvBindByName(column = "displayName")
	private String ownerName;
	
	@CsvBindByName(column = "email")
	private String ownerEmail;
	
	@CsvBindByName(column = "loanBal")
	private String loanBal;
	
	@CsvBindAndJoinByName(column = "([bal_00].+)", elementType = String.class)
	private MultiValuedMap<String, String> bal00;
	
	@CsvBindAndJoinByName(column = "([bal_30].+)", elementType = String.class)
	private MultiValuedMap<String, String> bal30;
	
	@CsvBindAndJoinByName(column = "([bal_60].+)", elementType = String.class)
	private MultiValuedMap<String, String> bal60;
	
	@CsvBindAndJoinByName(column = "([bal_90].+)", elementType = String.class)
	private MultiValuedMap<String, String> bal90;
	
	@CsvBindByName(column = "net")
	private String net;
	
	public double getBalance() {
		double loanBalance = Double.parseDouble(this.loanBal.replace("$", "").replaceAll(",",""));
		double balance00 = Double.parseDouble(this.bal00.values().toArray(new String[1])[3].replace("$", "").replaceAll(",",""));
		double balance30 = Double.parseDouble(this.bal00.values().toArray(new String[1])[2].replace("$", "").replaceAll(",",""));
		double balance60 = Double.parseDouble(this.bal00.values().toArray(new String[1])[1].replace("$", "").replaceAll(",",""));
		double balance90 = Double.parseDouble(this.bal00.values().toArray(new String[1])[4].replace("$", "").replaceAll(",",""));
		double net = Double.parseDouble(this.net.replace("$", "").replaceAll(",",""));
		
		if (net < 0 && loanBalance == 0 && balance00 == 0 && balance30 == 0 && balance60 == 0 && balance90 == 0) {
			return net;
		} else {
			return loanBalance + balance00;
		}
	}
	
	public double getOver30() {
		return Double.parseDouble(this.bal00.values().toArray(new String[1])[2].replace("$", "").replace("$", "").replaceAll(",",""));
	}
	
	public double getOver60() {
		return Double.parseDouble(this.bal00.values().toArray(new String[1])[1].replace("$", "").replace("$", "").replaceAll(",",""));
	}
	
	public double getOver90() {
		return Double.parseDouble(this.bal00.values().toArray(new String[1])[4].replace("$", "").replace("$", "").replaceAll(",",""));
	}
	
}
