package net.rockscience.util.date;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Wrapper class for {@link ZonedDateTime} that provides simple declaritive
 * access and conversions to a date in any timezone.
 *
 * @author Bob Damiano
 */
@ToString
@EqualsAndHashCode
public class MultizoneDateTime {

	public final static ZoneId utcZoneId = ZoneId.of("UTC");
	public final static ZoneId easternZoneId = ZoneId.of("America/New_York");

	// This is the internal date object for this instance. It can be created in any
	// Timezone
	// and this class will render and convert to any zone via the getters
	private ZonedDateTime internalDate;

	/**
	 * Private C'tor - you must use the static builders
	 */
	private MultizoneDateTime() {
	}

	/**
	 * Instantiate one from a {@link ZonedDateTime}
	 * 
	 * @param zdt
	 * @return
	 */
	public static MultizoneDateTime from(ZonedDateTime zdt) {
		MultizoneDateTime inst = new MultizoneDateTime();
		inst.internalDate = zdt;
		return inst;
	}

	/**
	 * Instantiate one from a {@link LocalDateTime} in the system timezone
	 * 
	 * @param systemLocalDate
	 * @return instance
	 */
	public static MultizoneDateTime fromSystem(LocalDateTime systemLocalDate) {
		if (systemLocalDate != null) {
			MultizoneDateTime inst = new MultizoneDateTime();
			inst.internalDate = systemLocalDate.atZone(ZoneId.systemDefault());
			return inst;
		}
		return null;
	}

	/**
	 * Instantiate one with a given offset from UTC
	 * 
	 * @param ldt
	 * @param long utcOffset
	 * @return
	 */
	public static MultizoneDateTime fromUtcOffset(LocalDateTime ldt, int utcOffset) {
		if (ldt != null) {
			MultizoneDateTime inst = new MultizoneDateTime();
			inst.internalDate = ldt.atZone(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(utcOffset)));
			return inst;
		}
		return null;
	}

	/**
	 * Instantiate one from a {@link LocalDateTime} in the given timezone
	 * 
	 * @param ldt
	 * @param zoneId
	 * @return instance
	 */
	public static MultizoneDateTime fromZone(LocalDateTime ldt, ZoneId zoneId) {
		if (ldt != null) {
			MultizoneDateTime inst = new MultizoneDateTime();
			inst.internalDate = ldt.atZone(zoneId);
			return inst;
		}
		return null;
	}

	/**
	 * Instantiate one from a {@link LocalDateTime} in UTC.
	 * Use this when you know your @{link LocalDateTime} is in UTC Time
	 * 
	 * @param ldt
	 * @return instance
	 */
	public static MultizoneDateTime fromUtc(LocalDateTime ldt) {
		if (ldt != null) {
			MultizoneDateTime inst = new MultizoneDateTime();
			inst.internalDate = ldt.atZone(utcZoneId);
			return inst;
		}
		return null;
	}

	/**
	 * Instantiate one from a {@link LocalDateTime} in ET
	 * Use this when you know your @{link LocalDateTime} is in Eastern Time
	 * This is here mostly for backward compatibility when creating dates
	 * from legacy data which has been persisted as ET
	 * (such as all of the LocalDateTime.now() usages).
	 * The Best practice for new code is to persist in UTC Local
	 * and use fromUtc() when instantiating one of these from persistence
	 * 
	 * @param ldt
	 * @return instance
	 */
	public static MultizoneDateTime fromEt(LocalDateTime ldt) {
		if (ldt != null) {
			MultizoneDateTime inst = new MultizoneDateTime();
			inst.internalDate = ldt.atZone(easternZoneId);
			return inst;
		}
		return null;
	}

	/**
	 * Instantiate one for right now using the system local TZ. This is the
	 * best/simplest way to make a now time for timestamping records, etc
	 * 
	 * @return instance
	 */
	public static MultizoneDateTime now() {
		MultizoneDateTime inst = new MultizoneDateTime();
		inst.internalDate = ZonedDateTime.now(ZoneId.systemDefault());
		return inst;
	}

	/**
	 * Wrapped version of isBefore() which compares UTC to UTC
	 * 
	 * @param other
	 * @return
	 */
	public boolean isBefore(MultizoneDateTime other) {
		return this.getUtc().isBefore(other.getUtc());
	}

	/**
	 * Wrapped version of isAfter() which compares UTC to UTC
	 * 
	 * @param other
	 * @return
	 */
	public boolean isAfter(MultizoneDateTime other) {
		return this.getUtc().isAfter(other.getUtc());
	}

	/**
	 * Change the internal date by the given number of minutes
	 * 
	 * @param minutes offset in minutes - may be positive or negative
	 */
	public void minutesOffset(long minutes) {
		if (internalDate != null) {
			internalDate = internalDate.plusMinutes(minutes);
		}
	}

	/**
	 * Change the internal date by the given number of days
	 * 
	 * @param days offset in days - may be positive or negative
	 */
	public void daysOffset(long days) {
		if (internalDate != null) {
			internalDate = internalDate.plusDays(days);
		}
	}

	public ZonedDateTime getAtZone(ZoneId zoneId) {
		if (zoneId.equals(internalDate.getZone())) {
			return internalDate;
		}
		return convertZonedDateTimeToNewZone(internalDate, zoneId);
	}

	/**
	 * Return a {@link ZonedDateTime} at UTC timezone
	 * 
	 * @return
	 */
	public ZonedDateTime getUtc() {
		return getAtZone(utcZoneId);
	}

	/**
	 * Return a {@link ZonedDateTime} at Eastern timezone
	 * 
	 * @return
	 */
	public ZonedDateTime getEt() {
		return getAtZone(easternZoneId);
	}

	/**
	 * Return a {@link ZonedDateTime} at the system timezone
	 * 
	 * @return
	 */
	public ZonedDateTime getSystemLocal() {
		return getAtZone(ZoneId.systemDefault());
	}

	public static LocalDateTime toZoneLocal(MultizoneDateTime mdt, ZoneId zoneId) {
		return mdt != null && zoneId != null ? mdt.getAtZone(zoneId).toLocalDateTime() : null;
	}

	/**
	 * Null-safe return of just the {@link LocalDateTime} part in ET timezone
	 * 
	 * @return
	 */
	public static LocalDateTime toEtLocal(MultizoneDateTime mdt) {
		return mdt != null ? mdt.getEt().toLocalDateTime() : null;
	}

	/**
	 * Null-safe return of just the {@link LocalDateTime} part in UTC timezone
	 * 
	 * @return
	 */
	public static LocalDateTime toUtcLocal(MultizoneDateTime mdt) {
		return mdt != null ? mdt.getUtc().toLocalDateTime() : null;
	}

	public static ZonedDateTime toUtcZoned(MultizoneDateTime mdt) {
		return mdt != null ? mdt.getUtc() : null;
	}

	/**
	 * Null-safe renderer of a {@link MultizoneDateTime} to server local timezone
	 * 
	 * @param mdt
	 * @return
	 */
	public static LocalDateTime toSystemLocal(MultizoneDateTime mdt) {
		return toZoneLocal(mdt, ZoneId.systemDefault());
	}

	/**
	 * Convert a ZonedDateTime to another zone
	 * 
	 * @param zonedDate - a date that belongs to a time zone
	 * @param toZone    - the zone we want to convert to
	 * @return
	 */
	private static ZonedDateTime convertZonedDateTimeToNewZone(ZonedDateTime zonedDate, ZoneId toZone) {
		if (zonedDate == null) {
			return null;
		}
		return zonedDate.withZoneSameInstant(toZone);
	}

	/**
	 * Wrap the format method.  Return null if null date
	 * @param fmt the {@link DateTimeFormatter} to use
	 * @return a formatted date string
	 */
	public String format(DateTimeFormatter fmt) {
		return internalDate != null ? internalDate.format(fmt) : null;
	}

}
