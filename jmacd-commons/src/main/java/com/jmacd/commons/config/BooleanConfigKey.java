package com.jmacd.commons.config;

import com.typesafe.config.Config;

public class BooleanConfigKey extends ConfigKey<Boolean> {

	public BooleanConfigKey(String key) {
		super(key);
	}

	@Override
	public Boolean getValue(Config config) {
		return config.getBoolean(key);
	}

	@Override
	public String getKeySuggestion() {
		return key + ": true";
	}

}
