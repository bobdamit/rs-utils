package net.rockscience.util.date;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * Wrapper class for {@link ZonedDateTime} that provides simple bean
 * access to a date in various timezones.
 * @author bdamiano
 */
@ToString
@EqualsAndHashCode
public class MultizoneDateTime {

	public final static ZoneId utcZoneId = ZoneId.of("UTC");
	public final static ZoneId easternZoneId = ZoneId.of("America/New_York");

	
	// This is the internal date object for this instance. It can be created in any Timezone
	// and this class will render ET and UTC (and anything else we want) via the getters
	private ZonedDateTime internalDate;
	
	/**
	 * Private C'tor - you must use the static builders
	 */
	private MultizoneDateTime() {}
	
	/**
	 * Instantiate one from a {@link ZonedDateTime}
	 * @param zdt
	 * @return
	 */
	public static MultizoneDateTime from(ZonedDateTime zdt) {
		MultizoneDateTime inst = new MultizoneDateTime();
		inst.internalDate = zdt;
		return inst;
	}
	
	/**
	 * Instantiate one from a {@link LocalDateTime} in UTC.
	 * Use this when you know your @{link LocalDateTime} is in UTC Time
	 * @param ldt
	 * @return
	 */
	public static MultizoneDateTime fromUtc(LocalDateTime ldt) {
		if(ldt != null) {
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
	 * @param ldt
	 * @return
	 */
	public static MultizoneDateTime fromEt(LocalDateTime ldt) {
		if(ldt != null) {
			MultizoneDateTime inst = new MultizoneDateTime();
			inst.internalDate = ldt.atZone(easternZoneId);
			return inst;
		}
		return null;
	}
	

	/**
	 * Instantiate one for right now using the system local TZ. This is the 
	 * best/simplest way to make a now time for timestamping records, etc
	 * @return
	 */
	public static MultizoneDateTime now() {
		MultizoneDateTime inst = new MultizoneDateTime();
		inst.internalDate = ZonedDateTime.now(ZoneId.systemDefault());
		return inst;
	}
	

	
	/**
	 * Wrapped version of isBefore() which compares UTC to UTC
	 * @param other
	 * @return
	 */
	public boolean isBefore(MultizoneDateTime other) {
		return this.getUtc().isBefore(other.getUtc());
	}
	
	/**
	 * Wrapped version of isAfter() which compares UTC to UTC
	 * @param other
	 * @return
	 */
	public boolean isAfter(MultizoneDateTime other) {
		return this.getUtc().isAfter(other.getUtc());
	}
	

	public ZonedDateTime getAtZone(ZoneId zoneId) {
		if(zoneId.equals(internalDate.getZone())) {
			return internalDate;
		}
		return convertZonedDateTimeToNewZone(internalDate, zoneId);
	}
	
	/**
	 * Return a {@link ZonedDateTime} at UTC timezone
	 * @return
	 */
	public ZonedDateTime getUtc() {
		return getAtZone(utcZoneId);
	}


	/**
	 * Return a {@link ZonedDateTime} at Eastern timezone
	 * @return
	 */
	public ZonedDateTime getEt() {
		return getAtZone(easternZoneId);
	}


	
	/**
	 * Return a {@link ZonedDateTime} at the system timezone
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
	 * @return
	 */
	public static LocalDateTime toEtLocal(MultizoneDateTime mdt) {
		return mdt != null ? mdt.getEt().toLocalDateTime() : null;
	}
	
	/**
	 * Null-safe return of just the {@link LocalDateTime} part in UTC timezone
	 * @return
	 */
	public static LocalDateTime toUtcLocal(MultizoneDateTime mdt) {
		return mdt != null ? mdt.getUtc().toLocalDateTime() : null;
	}

	public static ZonedDateTime toUtcZoned(MultizoneDateTime mdt) {
		return mdt != null ? mdt.getUtc() : null;
	}
	

		/**
	 * Convert a ZonedDateTime to another zone
	 * @param zonedDate - a date that belongs to a time zone
	 * @param toZone - the zone we want to convert to 
	 * @return
	 */
	private static ZonedDateTime convertZonedDateTimeToNewZone (ZonedDateTime zonedDate, ZoneId toZone) {
		if(zonedDate == null) {
			return null;
		}
		return zonedDate.withZoneSameInstant(toZone);
	}

}
