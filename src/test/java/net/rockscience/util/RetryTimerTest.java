package net.rockscience.util;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RetryTimerTest {

	private RetryTimer underTest;

    @BeforeEach
    public void setup(){
    }
        
    @Test
    public void testLoop() {
		int maxTries = 8;
		underTest = new RetryTimer(maxTries).fuzzy().withMinSeconds(5).withMaxSeconds(300);

		Integer lastResult = -11111111;
		for (int i = 0; i < maxTries-1; i++) {
			Integer nextSeconds = underTest.secondsToNextRetry(i);
			assertTrue(nextSeconds >= 5);
			// everyone should be longer than previous
			assertTrue(nextSeconds >= lastResult);
			lastResult = nextSeconds;
		}

		// one more should cause us to be done
		Integer next = underTest.secondsToNextRetry(maxTries);
		assertNull(next);
    }

	 @Test
	 public void testOutOfRange() {
		int maxTries = 4;
		underTest = new RetryTimer(maxTries).fuzzy().withMinSeconds(60);
		Integer next = underTest.secondsToNextRetry(500);
		assertNull(next);
	 }
}
    