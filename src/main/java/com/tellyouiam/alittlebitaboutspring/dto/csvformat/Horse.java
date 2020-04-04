package com.tellyouiam.alittlebitaboutspring.dto.csvformat;

import com.opencsv.bean.CsvBindAndJoinByName;
import static com.tellyouiam.alittlebitaboutspring.utils.StringHelper.getMultiMapSingleStringValue;
import static java.time.temporal.ChronoField.*;

import com.tellyouiam.alittlebitaboutspring.utils.StringHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;

@Getter
@Setter
public class Horse {

	@CsvBindAndJoinByName(column = "(id)|(externalId)", elementType = String.class)
	private MultiValuedMap<String, String> externalId;

	@CsvBindAndJoinByName(column = "(HorseName)|(Horse Name)|(Name|Horse)", elementType = String.class)
	private MultiValuedMap<String, String> name;
	
	@CsvBindAndJoinByName(column = "(dob|foaled)", elementType = String.class)
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
	
	@CsvBindAndJoinByName(column = "(AddedDate)|(Added Date)", elementType = String.class)
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

	private static final DateTimeFormatter AUSTRALIA_FORMAL_DATE_FORMAT;
	static {
		AUSTRALIA_FORMAL_DATE_FORMAT = new DateTimeFormatterBuilder()
				.appendValue(DAY_OF_MONTH, 2)
				.appendLiteral('/')
				.appendValue(MONTH_OF_YEAR, 2)
				.appendLiteral('/')
				.appendValue(YEAR, 4)
				.toFormatter();
	}

	private static final DateTimeFormatter AMERICAN_CUSTOM_LOCAL_DATE;
	static {
		AMERICAN_CUSTOM_LOCAL_DATE = new DateTimeFormatterBuilder()
				.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(DAY_OF_MONTH, 1,2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(YEAR, 2, 4, SignStyle.NEVER)
				.toFormatter()
				.withResolverStyle(ResolverStyle.STRICT);
	}

	public String toStandardObject(boolean isMDYFormat) {
		String externalId = getMultiMapSingleStringValue(this.externalId);
		String name = getMultiMapSingleStringValue(this.name);

		String foaled;
		String rawFoaled = getMultiMapSingleStringValue(this.foaled);
		if (!isMDYFormat && StringUtils.isNotEmpty(rawFoaled)) {
			foaled = LocalDate.parse(rawFoaled, AMERICAN_CUSTOM_LOCAL_DATE).format(AUSTRALIA_FORMAL_DATE_FORMAT);
		} else {
			foaled = rawFoaled;
		}

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
