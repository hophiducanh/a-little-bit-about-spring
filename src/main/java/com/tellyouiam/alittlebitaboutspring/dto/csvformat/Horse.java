package com.tellyouiam.alittlebitaboutspring.dto.csvformat;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.tellyouiam.alittlebitaboutspring.converter.LocalDateConverter;
import static com.tellyouiam.alittlebitaboutspring.utils.StringHelper.getMultiMapSingleStringValue;

import com.tellyouiam.alittlebitaboutspring.utils.StringHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;

@Getter
@Setter
public class Horse {
	// read from horse file first, standard columns order:
	// EXTERNAL ID, can leave blank if use hash code from the name as id
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
	
	@CsvBindAndJoinByName(column = "(id)|(externalId)", elementType = String.class)
	private MultiValuedMap<String, String> externalId;

	@CsvBindAndJoinByName(column = "(HorseName)|(Horse Name)|(Name|Horse)", elementType = String.class)
	private MultiValuedMap<String, String> name;
	
	@CsvBindAndJoinByName(column = "(dob|foaled)", elementType = String.class, converter = LocalDateConverter.class)
	private MultiValuedMap<String, String> foaled;
	
	@CsvBindAndJoinByName(column = "(sire)", elementType = String.class)
	private MultiValuedMap<String, String> sire;

	@CsvBindAndJoinByName(column = "(dam)", elementType = String.class)
	private MultiValuedMap<String, String> dam;

	@CsvBindAndJoinByName(column = "(color)", elementType = String.class)
	private MultiValuedMap<String, String> color;

	@CsvBindAndJoinByName(column = "(gender)|(sex)", elementType = String.class)
	private MultiValuedMap<String, String> sex;

	@CsvBindAndJoinByName(column = "(avatar)", elementType = String.class)
	private MultiValuedMap<String, String> avatar;
	
	@CsvBindAndJoinByName(column = "(AddedDate)|(Added Date)", elementType = String.class, converter = LocalDateConverter.class)
	private MultiValuedMap<String, String> addedDate;

	@CsvBindAndJoinByName(column = "(ActiveStatus)|(Active Status)", elementType = String.class)
	private MultiValuedMap<String, String> activeStatus;

	@CsvBindAndJoinByName(column = "(property)|(location)", elementType = String.class)
	private MultiValuedMap<String, String> horseLocation;

	@CsvBindAndJoinByName(column = "(CurrentStatus)|(Current Status)", elementType = String.class)
	private MultiValuedMap<String, String> horseStatus;

	@CsvBindAndJoinByName(column = "(type)", elementType = String.class)
	private MultiValuedMap<String, String> type;

	@CsvBindAndJoinByName(column = "(category)", elementType = String.class)
	private MultiValuedMap<String, String> category;

	@CsvBindAndJoinByName(column = "(schemes)|(BonusScheme)|(Bonus Scheme)", elementType = String.class)
	private MultiValuedMap<String, String> bonusScheme;

	@CsvBindAndJoinByName(column = "(NickName)|(Nick Name)", elementType = String.class)
	private MultiValuedMap<String, String> nickName;
	
	public String toStandardObject() {
		String externalId = getMultiMapSingleStringValue(this.externalId);
		String name = getMultiMapSingleStringValue(this.name);
		String foaled = getMultiMapSingleStringValue(this.foaled);
		String sire = getMultiMapSingleStringValue(this.sire);
		String dam = getMultiMapSingleStringValue(this.dam);
		String color = getMultiMapSingleStringValue(this.color);
		String sex = getMultiMapSingleStringValue(this.sex);
		String avatar = getMultiMapSingleStringValue(this.avatar);
		String addedDate = getMultiMapSingleStringValue(this.addedDate);
		String activeStatus = getMultiMapSingleStringValue(this.activeStatus);
		String horseLocation = getMultiMapSingleStringValue(this.horseLocation);
		String horseStatus = getMultiMapSingleStringValue(this.horseStatus);
		String type = getMultiMapSingleStringValue(this.type);
		String category = getMultiMapSingleStringValue(this.category);
		String bonusScheme = getMultiMapSingleStringValue(this.bonusScheme);
		String nickName = getMultiMapSingleStringValue(this.nickName);
		
		String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
				StringHelper.csvValue(externalId),
				StringHelper.csvValue(name),
				StringHelper.csvValue(foaled),
				StringHelper.csvValue(sire),
				StringHelper.csvValue(dam),
				StringHelper.csvValue(color),
				StringHelper.csvValue(sex),
				StringHelper.csvValue(avatar),
				StringHelper.csvValue(addedDate),
				StringHelper.csvValue(activeStatus),
				StringHelper.csvValue(horseLocation),
				StringHelper.csvValue(horseStatus),
				StringHelper.csvValue(type),
				StringHelper.csvValue(category),
				StringHelper.csvValue(bonusScheme),
				StringHelper.csvValue(nickName)
		);
		return rowBuilder;
	}
}
