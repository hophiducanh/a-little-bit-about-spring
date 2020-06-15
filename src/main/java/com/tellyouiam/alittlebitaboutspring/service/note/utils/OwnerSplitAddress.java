package com.tellyouiam.alittlebitaboutspring.service.note.utils;

import com.tellyouiam.alittlebitaboutspring.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class OwnerSplitAddress {
	private static final Logger logger = LoggerFactory.getLogger(OwnerSplitAddress.class);
	//Process Address
	private static final String ARDEX_ADDRESS_PATTERN = "(.*)\\s+(NSW|QLD|SA|S.A|TAS|VIC|Vic|WA|ACT|TASMANIA|VICTORIA|NT|N.T|NEW SOUTH WALES)$";
	private static final String SUBURB_PATTERN = ".*[a-z0-9]\\s+([A-Z]+[A-Z\\s]+)$";
	private static final String POSTAL_CODE_PATTERN_1 = ".*\\s+([0-9-]+)$";
	public static final String POSTAL_CODE_PATTERN_2 = "^([0-9-]+)\\s*";
	
	// all other countries we found after process by sublime REGEX for current
	// trainer
	private static List<String> TRAINER_COUNTRIES = new ArrayList<>(Arrays.asList(
			
			"AUS",
			
			"AUSTRALIA",
			
			"CHINA",
			
			"ENGLAND",
			
			"FRANCE",
			
			"HONG KONG",
			
			"HONG KONG NEW TERRITORIES",
			
			"INDONESIA",
			
			"IRELAND",
			
			"JAPAN",
			
			"MALAYSIA",
			
			"NEW ZEALAND",
			
			"PHILLIPINES",
			
			"SIGNAPORE",
			
			"SCOTLAND",
			
			"SINGAPORE",
			
			"SRI LANKA",
			
			"STH AFRICA",
			
			"SWITZERLAND",
			
			"THAILAND",
			
			"U K",
			
			"UK",
			
			"UNITED KINGDOM",
			
			"UNITED STATES OF AMERICA",
			
			"USA",
			
			"VIETNAM"));
	
	//remove case-insensitive
	private static String removeText(String input, String remove) {
		return input.replaceAll(String.format("(?i)%s", remove), "").trim();
	}
	
	public static Map<String, String> splitAddress(String input) {
		Map<String, String> result = new HashMap<>();
		
		String address = input.trim();
		String suburb = "";
		String state = "";
		String postcode = "";
		String country = "";
		
		// if address end with postal code
		// sample: "Racecourse 1 Turf Club Avenue SINGAPORE 738078" "26 Melliodora Crescent GREENSBOROUGH 3088 VIC"
		String number = StringHelper.extract(address, POSTAL_CODE_PATTERN_1);
		if (!isEmpty(number)) {
			postcode = number;
			address = removeText(address, postcode);
		}
		
		// if this address belong to other countries (not Australia)
		for (String c : TRAINER_COUNTRIES) {
			if (address.toUpperCase().endsWith(c)) {
				country = c;
				break;
			}
		}
		
		if (!isEmpty(country)) {
			try {
				address = removeText(address, country);
			} catch (Exception e) {
				logger.error("Error here {} >> {}", address, country);
			}
		}
		
		if (isEmpty(postcode)) {
			number = StringHelper.extract(address, POSTAL_CODE_PATTERN_1);
			if (!isEmpty(number)) {
				postcode = number;
				address = removeText(address, postcode);
			}
		}
		
		// try to detect Australia address (contain provided states)
		Pattern ardexAddressPattern = Pattern.compile(ARDEX_ADDRESS_PATTERN);
		Matcher matcher = ardexAddressPattern.matcher(address);
		
		// pattern matched (contain Australia states)
		if (matcher.matches()) {
			country = "Australia";
			
			// group 2 is state info
			state = matcher.group(2).trim();
			address = removeText(address, state);
			
			// sample 1: "26 Melliodora Crescent GREENSBOROUGH 3088"
			// sample 2: "Ngaroma 736 Windellama Road SUNDARY"
			String beforeStateText = matcher.group(1).trim();
			
			if (isEmpty(postcode)) {
				// extract postal code >> 4 digits at end of text (postal code is before state)
				postcode = StringHelper.extract(beforeStateText, POSTAL_CODE_PATTERN_1);
				
				// reduce address if found postal code
				if (!isEmpty(postcode)) {
					address = removeText(beforeStateText, postcode);
				} else {
					// this case postal code is after state)
					address = beforeStateText;
				}
			}
			
			// in group 1 we can extract suburb info >> all-upper-case text
			// sample: "GREENSBOROUGH"
			if (!isEmpty(address)) {
				
				// only can extract suburb if text mix beetween lower case and upper case
				// sample: 26 Melliodora Crescent GREENSBOROUGH >> extract "GREENSBOROUGH"
				if (!isEmpty(StringHelper.extract(address, ".*([a-z]).*"))) {
					suburb = StringHelper.extract(address, SUBURB_PATTERN);
					
					// continue reduce address if found suburd
					if (!isEmpty(suburb))
						address = removeText(address, suburb);
				}
			}
		} else {
			// process for other countries
			
			// try to extract postal code & suburb info
			if (!isEmpty(address)) {
				if (isEmpty(postcode)) {
					// extract post code number if available
					postcode = StringHelper.extract(address, POSTAL_CODE_PATTERN_1);
					
					// remain text: "11-1, Roppongi 6-Chroe Minato-ku TOKYO"
					if (!isEmpty(postcode))
						address = removeText(address, postcode);
				}
				
				// extract suburb if available (consecutive upper case text)
				if (!isEmpty(address)) {
					// only can extract suburb if text mix beetween lower case and upper case
					// sample: 26 Melliodora Crescent GREENSBOROUGH >> extract "GREENSBOROUGH"
					if (!isEmpty(StringHelper.extract(address, ".*([a-z]).*"))) {
						suburb = StringHelper.extract(address, SUBURB_PATTERN);
						
						if (!isEmpty(suburb))
							address = removeText(address, suburb);
						
						// try to extract postcal code if available
						if (!isEmpty(address) && isEmpty(postcode)) {
							postcode = StringHelper.extract(address, POSTAL_CODE_PATTERN_1);
							if (!isEmpty(postcode))
								address = removeText(address, postcode);
						}
					}
				}
			}
		}
		
		result.put("address", address);
		result.put("suburb", suburb);
		result.put("state", state);
		result.put("postcode", postcode);
		result.put("country", country);
		
		return result;
	}
}
