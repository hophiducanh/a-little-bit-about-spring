package com.tellyouiam.alittlebitaboutspring.dto.csvformat;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import com.tellyouiam.alittlebitaboutspring.converter.LocalDateConverter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;

import java.time.LocalDate;

@Getter
@Setter
public class Horse {
	// read from horse file first, standard columns order:
	// EXTERNAL ID, can leave blank if use hash code from name as id
	// NAME
	// FOALED
	// SIRE
	// DAM
	// COLOUR
	// SEX
	// AVATAR
	// ADDED DATE
	// STATUS (active/inactive)
	// CURRENT LOCATION
	// CURRENT STATUS
	// TYPE (Race Horse/ Stallion/ Speller/ Brood Mare/ Yearling)
	// CATEGORY
	// BONUS SCHEME
	// NICK NAME
	
	@CsvBindAndJoinByName(column = "(id|externalId)", elementType = String.class)
	private MultiValuedMap<String, String> externalId;

	@CsvBindAndJoinByName(column = "(HorseName|Horse Name|Name|Horse)", elementType = String.class)
	private MultiValuedMap<String, String> name;
	
//	@CsvBindAndJoinByName(column = "(dob|foaled)", elementType = LocalDate.class)
	@CsvCustomBindByName(converter = LocalDateConverter.class)
	private String foaled;
	
	@CsvBindByName(column = "sire")
	private String sire;

	@CsvBindByName(column = "dam")
	private String dam;

	@CsvBindByName(column = "color")
	private String color;

	@CsvBindAndJoinByName(column = "(gender|sex)", elementType = String.class)
	private MultiValuedMap<String, String> sex;

	@CsvBindByName(column = "avatar")
	private String avatar;

	@CsvBindAndJoinByName(column = "(AddedDate|Added Date)", elementType = String.class)
	@CsvDate("dd/MM/yyyy")
	private MultiValuedMap<LocalDate, LocalDate> addedDate;

	@CsvBindAndJoinByName(column = "(ActiveStatus|Active Status)", elementType = String.class)
	private MultiValuedMap<String, String> activeStatus;

	@CsvBindAndJoinByName(column = "(property|location)", elementType = String.class)
	private MultiValuedMap<String, String> horseLocation;

	@CsvBindAndJoinByName(column = "(CurrentStatus|Current Status)", elementType = String.class)
	private MultiValuedMap<String, String> horseStatus;

	@CsvBindByName(column = "type")
	private String type;

	@CsvBindByName(column = "category")
	private String category;

	@CsvBindAndJoinByName(column = "(schemes|BonusScheme|Bonus Scheme)", elementType = String.class)
	private MultiValuedMap<String, String> bonusScheme;

	@CsvBindAndJoinByName(column = "(NickName|Nick Name)", elementType = String.class)
	private MultiValuedMap<String, String> nickName;
}
