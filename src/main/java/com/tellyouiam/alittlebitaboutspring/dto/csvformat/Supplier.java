package com.tellyouiam.alittlebitaboutspring.dto.csvformat;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;

@Getter
@Setter
public class Supplier {
	
	@CsvBindAndJoinByName(column = "(name)", elementType = String.class)
	private MultiValuedMap<String, String> name;
	
	@CsvBindAndJoinByName(column = "(firstName)|(first name)", elementType = String.class)
	private MultiValuedMap<String, String> firstName;
	
	@CsvBindAndJoinByName(column = "(lastName)|(last name)", elementType = String.class)
	private MultiValuedMap<String, String> lastName;
	
	@CsvBindByName
	private String mobile;
	
	@CsvBindByName
	private String phone;
	
	@CsvBindByName
	private String fax;
	
	@CsvBindAndJoinByName(column = "(email)", elementType = String.class)
	private MultiValuedMap<String, String> email;
	
	@CsvBindAndJoinByName(column = "(address)", elementType = String.class)
	private MultiValuedMap<String, String> address;
	
	@CsvBindAndJoinByName(column = "(suburb)|(city)", elementType = String.class)
	private MultiValuedMap<String, String> suburb;
	
	private MultiValuedMap<String, String> postCode;
	
	@CsvBindByName
	private String state;
	
	@CsvBindByName
	private String country;
	
	@CsvBindAndJoinByName(column = "(gst)", elementType = String.class)
	private MultiValuedMap<String, String> gst;
	
	@CsvBindAndJoinByName(column = "(debtor)", elementType = String.class)
	private MultiValuedMap<String, String> debtor;
	
	@CsvBindAndJoinByName(column = "(abn)", elementType = String.class)
	private MultiValuedMap<String, String> abn;
	
	@CsvBindAndJoinByName(column = "(bankAccountName)", elementType = String.class)
	private MultiValuedMap<String, String> bankAccountName;
	
	@CsvBindAndJoinByName(column = "(bankAccountNumber)", elementType = String.class)
	private MultiValuedMap<String, String> bankAccountNumber;
}
