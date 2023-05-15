package com.jmacd.commons;

public interface JMacDChecks {

	default boolean isEmpty(String value) {
		return !isNonEmpty(value);
	}

	default boolean isNonEmpty(String value) {
		return ((value != null) && (value.trim().isEmpty() == false));
	}

	default boolean isNonEmpty(String... values) {
		boolean result = true;

		for (String string : values) {
			result = result && isNonEmpty(string);
		}

		return result;
	}

}
