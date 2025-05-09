package net.rockscience.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 * Handy class to wrap the concept of a CRON for 
 * DayS of week and day of month
 * @author Bob Damiano
 */

@Data
public class TaskCron {

	private Set<DayOfWeek> daysOfWeek = new HashSet<>();
	private Integer dayOfMonth;
	
	public boolean isToday() {
		LocalDate today = LocalDate.now();
		if(daysOfWeek.contains(today.getDayOfWeek())) {
			return true;
		}
		
		if(dayOfMonth != null && dayOfMonth.equals(today.getDayOfMonth())) {
			return true;
		}
		
		return false;
	}

    	/**
	 * Return a bit mask for the 7 days of week
	 * By convention (java.time), Monday is day 1.
	 * bit 0 corresponds to monday so 
	 * Mon, Wed, Thur would yield a bit mask of 0b1101
	 * @return
	 */
	public Integer getDayOfWeekBitMask() {
		Integer mask = 0;
		int exponent = 0;
		for(DayOfWeek dow : DayOfWeek.values()) {
			if(daysOfWeek.contains(dow)) {
				mask += (int)(Math.pow(2, exponent));
			}
			exponent++;
		}
		return mask;
	}
	
	/**
	 * Populate the DayOfWeek set from a bitmask of days.
	 * Example: 0b11101 would be Mon,Wed,Thr,Fri
	 * @param mask
	 * @return
	 */
	public TaskCron withDayOfWeekMask(Integer mask) {
		for(DayOfWeek dow : DayOfWeek.values()) {
			if(mask % 2 != 0) {
				daysOfWeek.add(dow);
			}
			mask /= 2;
		}
		return this;
	}

    public TaskCron withDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        return this;
    }
 	
}
