package com.jmacd.commons.config;

import com.typesafe.config.Config;

import lombok.Data;

@Data
public abstract class ConfigKey<T> {

	protected final String key;

	public ConfigKey(String key) {
		super();

		this.key = key;
	}

	public abstract T getValue(Config config);

	public abstract String getKeySuggestion();

}
