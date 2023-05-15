package com.jmacd.commons;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Results<T> {

	private final boolean succcessful;
	private final T value;

	public Results(boolean succcessful, T value) {
		super();

		this.succcessful = succcessful;
		this.value = value;
	}
}