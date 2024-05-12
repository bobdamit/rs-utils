package net.rockscience.util.enumz;

/**
 * Helper to convert Enums to and from a stable integer code for persistence
 */
public class StableOrderEnum {
	private StableOrderEnum(){}

	/** 
	 * Interface for Enums to implement so they can provide a stable
	 * integer code regardless of order
	 */
	public interface HasStableCode {
		public Integer getStableCode();
	}

	/**
	 * Safely return stable code of an enum instance.
	 * Returns null if null instance is provided
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T extends HasStableCode> Integer toCode(T t) {
		return t == null ? null : t.getStableCode();
	}

	/**
	 * Linear lookup of all instances of the enum to fine one with a certain
	 * stable code
	 * @param <T>
	 * @param values
	 * @param c
	 * @return
	 */
	public static <T extends HasStableCode> T fromCode(T[] values, Integer c) {
		for (T v : values) {
			if(v.getStableCode().equals(c)) {
				return v;
			}
		}
		return null;
	}

}
