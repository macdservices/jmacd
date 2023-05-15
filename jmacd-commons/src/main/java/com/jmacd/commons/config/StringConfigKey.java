package com.jmacd.commons.config;

import com.typesafe.config.Config;

public class StringConfigKey extends ConfigKey<String> {

	public StringConfigKey(String key) {
		super(key);
	}

	@Override
	public String getValue(Config config) {
		return config.getString(key);
	}

	@Override
	public String getKeySuggestion() {
		return key + ": \"value\"";
	}

}
