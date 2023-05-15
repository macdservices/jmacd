package com.jmacd.commons;

import lombok.Data;

@Data
public class EncryptedString {

	private String value;

	public EncryptedString(String value) {
		super();

		this.value = value;
	}

}
