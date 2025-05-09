package net.rockscience.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Simple class to measure time spans
 * @author Bob Damiano
 */
public class Timespan {
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private long warnThreshold;
	
	/**
	 * Constructor - initializes the start time to now
	 */
	public Timespan() {
		startTime = LocalDateTime.now();
	}
	
	public Timespan(long warnThresholdMS) {
		this();
		this.warnThreshold = warnThresholdMS;
	}

	/**
	 * Set the end time to now
	 */
	public Timespan end() {
		endTime = LocalDateTime.now();
		return this;
	}

	public boolean isWarn() {
		return (warnThreshold > 0L && getEllapsedMilliseconds() > warnThreshold);
	}
	

	/**
	 * Return the ellapsed time in Milliseconds between 
	 * start and end times
	 * @return
	 */
	public long getEllapsedMilliseconds() {
		if(null == endTime || null == startTime) {
			return -1l;
		}

		return endTime.toInstant(ZoneOffset.UTC).toEpochMilli() - 
				startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
	}
	
	@Override
	public String toString() {
		return String.format("time-span:%dmS  warn threshold:%dms", getEllapsedMilliseconds(), warnThreshold);
	}
}
