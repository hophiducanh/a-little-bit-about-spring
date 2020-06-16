package com.tellyouiam.alittlebitaboutspring.service.note.consts;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public class NoteConst {
	
	public static final String CSV_LINE_SEPARATOR = ",";
	public static final String CSV_LINE_END = "\n";
	public static final String QUOTE_CHAR = "\"";
	
	public static final String REMOVE_BANK_LINES_PATTERN = "(?m)^[,]*$\n";
	public static final String REMOVE_LINE_BREAK_PATTERN = "\nCT\\b";
	public static final String REMOVE_INVALID_SHARES_PATTERN = "\\bInt.Party\\b";
	public static final String CORRECT_HORSE_NAME_PATTERN = "^([^,]*)(?=\\s\\(.*).*$";
	public static final String TRYING_SHARE_COLUMN_POSITION_PATTERN = "(?m)^(([\\d]{1,3})(\\.)([\\d]{1,2})%)";
	public static final String TRIM_HORSE_NAME_PATTERN = "(?m)^\\s";
	public static final String MOVE_HORSE_TO_CORRECT_LINE_PATTERN = "(?m)^([^,].*)\\n,(?=([\\d]{1,3})?(\\.)?([\\d]{1,2})?%)";
	public static final String EXTRACT_DEPARTED_DATE_OF_HORSE_PATTERN =
			"(?m)^([^,].*)\\s\\(\\s.*([\\s]+)([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
	public static final String NORMAL_OWNERSHIP_EXPORTED_DATE_PATTERN =
			"(?m)(\\bPrinted\\b[:\\s]+)([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
	
	public static final String ARDEX_OWNERSHIP_EXPORTED_DATE_PATTERN = "(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),\\s" +
			"((\\(0[1-9]|[12][0-9]|3[01])\\s" +
			"(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?),\\s" +
			"((19|20)\\d\\d))";
	
	public static final String MIXING_COMMS_FINANCE_EMAIL_PATTERN = "\"?\\bAccs\\b:\\s((.+) (?=(\\bComms\\b)))\\bComms\\b:\\s((.+)(\\.[a-zA-Z;]+)(?=,))\"?";
	
	public static final String REMOVE_UNNECESSARY_DATA =
			"(?m)^(?!((,)?Share %)|(.*(?=([\\d]{1,3})(\\.)([\\d]{1,2})%))).*$(\\n)?";
	public static final String CSV_HORSE_COUNT_PATTERN = "(?m)^(.+)Horses([,]+)$";
	public static final int IGNORED_NON_DATA_LINE_THRESHOLD = 9;
	
	public static final String WINDOW_OUTPUT_FILE_PATH = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\";
	public static final String UNIX_OUTPUT_FILE_PATH = "/home/logbasex/Desktop/data/";
	public static final String CT_IN_DISPLAY_NAME_PATTERN = "\\bCT:";
	
	public static final String HORSE_RECORDS_PATTERN = "([\\d]+)\\sRecords"; //like: 162 records
	
	public static final DateTimeFormatter AUSTRALIA_CUSTOM_DATE_FORMAT;
	static {
		AUSTRALIA_CUSTOM_DATE_FORMAT = new DateTimeFormatterBuilder()
				.appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(YEAR, 2, 4, SignStyle.NEVER)
				.toFormatter()
				.withResolverStyle(ResolverStyle.STRICT);
	}
	
	public static final DateTimeFormatter AUSTRALIA_FORMAL_DATE_FORMAT;
	static {
		AUSTRALIA_FORMAL_DATE_FORMAT = new DateTimeFormatterBuilder()
				.appendValue(DAY_OF_MONTH, 2)
				.appendLiteral('/')
				.appendValue(MONTH_OF_YEAR, 2)
				.appendLiteral('/')
				.appendValue(YEAR, 4)
				.toFormatter()
				.withResolverStyle(ResolverStyle.STRICT);
	}
	
	public static final DateTimeFormatter AMERICAN_CUSTOM_DATE_FORMAT;
	static {
		AMERICAN_CUSTOM_DATE_FORMAT = new DateTimeFormatterBuilder()
				.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(YEAR, 2, 4, SignStyle.NEVER)
				.toFormatter()
				.withResolverStyle(ResolverStyle.STRICT);
	}
	
	//ResolverStyle should using yyyy instead of uuuu
	public static final DateTimeFormatter ARDEX_DATE_FORMAT;
	static {
		ARDEX_DATE_FORMAT = new DateTimeFormatterBuilder()
				.appendPattern("dd MMMM, uuuu")
				.parseCaseSensitive()
				.toFormatter()
				.withResolverStyle(ResolverStyle.STRICT);
	}
}
