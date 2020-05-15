package com.tellyouiam.alittlebitaboutspring.entity.csvformat;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import com.tellyouiam.alittlebitaboutspring.converter.CustomJoinNameDoubleStringConverter;
import static com.tellyouiam.alittlebitaboutspring.utils.StringHelper.getMultiMapSingleDoubleValue;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;

@Getter
@Setter
public class OpeningBalance {
	@CsvBindByName(column = "displayName")
	private String ownerName;

	@CsvBindAndJoinByName(column = "(loanBal)", elementType = String.class, converter = CustomJoinNameDoubleStringConverter.class)
	private MultiValuedMap<String, Double> loanBal;

	@CsvBindAndJoinByName(column = "(bal_00)|(Balance)", elementType = String.class, converter = CustomJoinNameDoubleStringConverter.class)
	private MultiValuedMap<String, Double> bal00;

	@CsvBindAndJoinByName(column = "bal_30", elementType = String.class, converter = CustomJoinNameDoubleStringConverter.class)
	private MultiValuedMap<String, Double> bal30;

	@CsvBindAndJoinByName(column = "bal_60", elementType = String.class, converter = CustomJoinNameDoubleStringConverter.class)
	private MultiValuedMap<String, Double> bal60;

	@CsvBindAndJoinByName(column = "^(bal_90).+$", elementType = String.class, converter = CustomJoinNameDoubleStringConverter.class)
	private MultiValuedMap<String, Double> bal90;

	@CsvBindAndJoinByName(column = "net", elementType = String.class, converter = CustomJoinNameDoubleStringConverter.class)
	private MultiValuedMap<String, Double> net;
	
	public double getBalance() {
		double loanBalance = getMultiMapSingleDoubleValue(this.loanBal);
		double balance00 = getMultiMapSingleDoubleValue(this.bal00);
		double balance30 = getMultiMapSingleDoubleValue(this.bal30);
		double balance60 = getMultiMapSingleDoubleValue(this.bal60);
		double balance90 = getMultiMapSingleDoubleValue(this.bal90);
		double net = getMultiMapSingleDoubleValue(this.net);

		if (net < 0 && loanBalance == 0 && balance00 == 0 && balance30 == 0 && balance60 == 0 && balance90 == 0) {
			return net;
		} else {
			return loanBalance + balance00;
		}
	}
	
	public double getOver30() {
		return getMultiMapSingleDoubleValue(this.bal30);
	}
	
	public double getOver60() {
		return getMultiMapSingleDoubleValue(this.bal60);
	}
	
	public double getOver90() {
		return getMultiMapSingleDoubleValue(this.bal90);
	}
}
