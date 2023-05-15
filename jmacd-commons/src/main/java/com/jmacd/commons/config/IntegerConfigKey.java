package com.jmacd.commons.config;

import com.typesafe.config.Config;

public class IntegerConfigKey extends ConfigKey<Integer> {

	public IntegerConfigKey(String key) {
		super(key);
	}

	@Override
	public Integer getValue(Config config) {
		return config.getInt(key);
	}

	@Override
	public String getKeySuggestion() {
		return key + ": 1";
	}

}
