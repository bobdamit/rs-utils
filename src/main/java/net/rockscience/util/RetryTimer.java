package net.rockscience.util;

import java.util.Random;

public class RetryTimer {

	private final int maxRetries;
	private boolean fuzzy;
	private int minSeconds = 60;
	private int maxSeconds = 0;

	public RetryTimer(int max) {
		this.maxRetries = max;
	}
	public RetryTimer fuzzy() {
		this.fuzzy = true;
		return this;
	}
	public RetryTimer withMinSeconds(int min) {
		this.minSeconds = min;
		return this;
	}
	public RetryTimer withMaxSeconds(int m) {
		this.maxSeconds = m;
		return this;
	}

	/**
	 * Given the attempt count, return seconds until next retry with an escelating time.
	 * Returns null when there are no more retries
	 * @param currentAttempt
	 * @return
	 */
	Integer secondsToNextRetry(int currentAttempt) {
		currentAttempt = Math.max(0,currentAttempt);
		int triesRemaining = this.maxRetries - currentAttempt;
		if(triesRemaining  <= 0) {
			return null;
		}
		int baseSeconds = minSeconds;
		if(fuzzy) {
			baseSeconds = baseSeconds + new Random().nextInt(10);
		}

		int next =  baseSeconds + (int)(Math.pow(currentAttempt, 2) * 10);

		if(maxSeconds > 0) {
			next = Math.min(next, maxSeconds);
		}
		return next;
	}
	
}
