package net.rockscience.util.date;



import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class MultizoneDateTimeTest {

	@Test
	public void testFromSystemLocal() {
		MultizoneDateTime underTest = MultizoneDateTime.now();

		assertNotEquals(underTest.getEt(), underTest.getUtc());
		
		// compare the local date parts
		assertTrue(MultizoneDateTime.toEtLocal(underTest).isBefore(MultizoneDateTime.toUtcLocal(underTest)));
	}

	@Test
	public void testFromUtc() {
		MultizoneDateTime underTest = MultizoneDateTime.fromUtc(LocalDateTime.now().plusDays(1));

		assertNotEquals(underTest.getEt(), underTest.getUtc());
		
		// compare the local date parts
		assertTrue(MultizoneDateTime.toEtLocal(underTest).isBefore(MultizoneDateTime.toUtcLocal(underTest)));
	}

	@Test
	public void testFromEastern() {
		MultizoneDateTime underTest = MultizoneDateTime.fromEt(LocalDateTime.now().plusDays(20));

		assertNotEquals(underTest.getEt(), underTest.getUtc());
		
		// compare the local date parts
		assertTrue(MultizoneDateTime.toEtLocal(underTest).isBefore(MultizoneDateTime.toUtcLocal(underTest)));
	}
	
	@Test
	public void testFromCentral() {
		MultizoneDateTime underTest = MultizoneDateTime.fromEt(LocalDateTime.now().minusDays(200));
		assertNotEquals(underTest.getEt(), underTest.getUtc());
		
		// compare the local date parts
		assertTrue(MultizoneDateTime.toEtLocal(underTest).isBefore(MultizoneDateTime.toUtcLocal(underTest)));
	}
	
	
	@Test
	public void testFromZdt() {
		ZoneId zid = ZoneId.of("Asia/Tokyo");
		
		MultizoneDateTime underTest = MultizoneDateTime.from(ZonedDateTime.now(zid));
		
		assertNotEquals(underTest.getEt(), underTest.getUtc());

		// compare the local date parts
		assertTrue(MultizoneDateTime.toEtLocal(underTest).isBefore(MultizoneDateTime.toUtcLocal(underTest)));
	}
		
	
	@Test
	public void testBeforeDifferentTimesSameZone() {
		MultizoneDateTime mzd1 = MultizoneDateTime.fromEt(LocalDateTime.now().minusHours(1));
		MultizoneDateTime mzd2 = MultizoneDateTime.fromEt(LocalDateTime.now().plusMinutes(10));

		assertTrue(mzd1.getSystemLocal().isBefore(mzd2.getSystemLocal()));

		assertTrue(mzd1.isBefore(mzd2));
	}

	@Test
	public void testAfterDifferentTimesSameZone() {
		MultizoneDateTime mzd1 = MultizoneDateTime.fromEt(LocalDateTime.now().plusHours(1));
		MultizoneDateTime mzd2 = MultizoneDateTime.fromEt(LocalDateTime.now().minusMinutes(10));
	
		assertTrue(mzd1.isAfter(mzd2));
	}

	@Test
	public void testBeforeDifferentTimesDifferentZone() {
		MultizoneDateTime mzd1 = MultizoneDateTime.fromEt(LocalDateTime.now().minusHours(5));
		MultizoneDateTime mzd2 = MultizoneDateTime.fromUtc(LocalDateTime.now().plusMinutes(10));

		assertTrue(mzd1.isBefore(mzd2));
	}

	@Test
	public void testFormatLocal() {
		MultizoneDateTime mzd1 = MultizoneDateTime.now();
		String fmt = mzd1.format(DateTimeFormatter.ISO_DATE_TIME);

		assertNotNull(fmt);
	}

	@Test
	public void testFormatZoned() {
		MultizoneDateTime mzd1 = MultizoneDateTime.now();
		String fmt = mzd1.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		assertNotNull(fmt);
	}
}
