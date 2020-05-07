package com.tellyouiam.alittlebitaboutspring.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NumberHelper {
	private static final int MONDAY = 0b1000000;//64
	private static final int TUESDAY = 0b0100000;//32
	private static final int WEDNESDAY = 0b0010000;//16
	private static final int THURSDAY = 0b0001000;//8
	private static final int FRIDAY = 0b000100;//4
	private static final int SATURDAY = 0b0000010;//2
	private static final int SUNDAY = 0b0000001; //1
	
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean sunday;
	
	public NumberHelper(int flag) {
		monday = (flag & MONDAY) == MONDAY;
		tuesday = (flag & TUESDAY) == TUESDAY;
		wednesday = (flag & WEDNESDAY) == WEDNESDAY;
		thursday = (flag & THURSDAY) == THURSDAY;
		friday = (flag & FRIDAY) == FRIDAY;
		saturday = (flag & SATURDAY) == SATURDAY;
		sunday = (flag & SUNDAY) == SUNDAY;
	}
	
	private static Integer getRepeatType(NumberHelper repeatType) {
		if (repeatType == null) {
			return 0;
		}
		return ((repeatType.isMonday() ? 1 : 0) << 6)
				| ((repeatType.isTuesday() ? 1 : 0) << 5)
				| ((repeatType.isWednesday() ? 1 : 0) << 4)
				| ((repeatType.isThursday() ? 1 : 0) << 3)
				| ((repeatType.isFriday() ? 1 : 0) << 2)
				| ((repeatType.isSaturday() ? 1 : 0) << 1)
				| (repeatType.isSunday() ? 1 : 0);
	}
	
	public static void main(String[] args) {
		int i = getRepeatType(new NumberHelper(1));
		System.out.println(i);
	}
}
