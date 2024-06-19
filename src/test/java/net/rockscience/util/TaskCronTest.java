package net.rockscience.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskCronTest {

    private TaskCron underTest;

    @BeforeEach
    void setup() {
        underTest = new TaskCron();
    }

    @Test
	void test() {
		
		underTest.setDayOfMonth(15);
		assertEquals(Integer.valueOf(0b0), underTest.getDayOfWeekBitMask());
		assertFalse(underTest.isToday());
	}
	
	@Test
	void testWed() {
		underTest.setDayOfMonth(15);
		underTest.getDaysOfWeek().add(DayOfWeek.WEDNESDAY);
		assertEquals(Integer.valueOf(0b100), underTest.getDayOfWeekBitMask());
	}

	
	@Test
	void testMon() {
		underTest.setDayOfMonth(15);
		underTest.getDaysOfWeek().add(DayOfWeek.MONDAY);
		
		Integer mask = underTest.getDayOfWeekBitMask();
		assertEquals(Integer.valueOf(0b1), mask);
		
		TaskCron other = new TaskCron().withDayOfWeekMask(mask);
		assertEquals(other.getDaysOfWeek(), underTest.getDaysOfWeek());
	}

	@Test
	void testSun() {
		underTest.setDayOfMonth(15);
		underTest.getDaysOfWeek().add(DayOfWeek.SUNDAY);
		Integer mask = underTest.getDayOfWeekBitMask();

		assertEquals(Integer.valueOf(0b1000000), mask);
		TaskCron other = new TaskCron().withDayOfWeekMask(mask);
		
		assertEquals(other.getDaysOfWeek(), underTest.getDaysOfWeek());
	}

	@Test
	void testMWTF() {
		underTest.setDayOfMonth(15);
		Collections.addAll(underTest.getDaysOfWeek(), DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY, DayOfWeek.THURSDAY);
		
		Integer mask = underTest.getDayOfWeekBitMask();
		assertEquals(Integer.valueOf(0b11101), mask);
		TaskCron other = new TaskCron().withDayOfWeekMask(mask);
		assertEquals(other.getDaysOfWeek(), underTest.getDaysOfWeek());
	}

	
	@Test
	void testEveryDay() {
		underTest.setDayOfMonth(15);
		Collections.addAll(underTest.getDaysOfWeek(), DayOfWeek.values());
		
		Integer mask = underTest.getDayOfWeekBitMask();
		// all 7 bits should be set
		assertEquals(Integer.valueOf(0b1111111), mask);
		TaskCron other = new TaskCron().withDayOfWeekMask(mask);
		assertEquals(other.getDaysOfWeek(), underTest.getDaysOfWeek());
		
		assertTrue(underTest.isToday());
	}
}
