package com.jmacd.commons.config;

import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.Config;

public class ConfigKeys {

	private List<ConfigKey<?>> configKeys = new ArrayList<>();

	public ConfigKeys() {
		super();
	}

	public void verifyConfigKeys(String configFilePath, Config config) {
		List<String> missingList = new ArrayList<>();

		for (ConfigKey<?> configKey : configKeys) {
			if (config.hasPath(configKey.getKey()) == false) {
				missingList.add(configKey.getKeySuggestion());
			}
		}

		if (missingList.size() > 0) {
			System.out.println(//
					"" //
							+ "--------------------------------------------------------" + "\n" //
							+ "The following keys are missing from config, " + configFilePath + "\n" //
							+ String.join("\n", missingList) + "\n" //
							+ "--------------------------------------------------------");

			throw new RuntimeException("Missing Configuration Values");
		}
	}

	public StringConfigKey createStringKey(String key) {
		StringConfigKey stringConfigKey = new StringConfigKey(key);

		configKeys.add(stringConfigKey);

		return stringConfigKey;
	}

	public IntegerConfigKey createIntegerKey(String key) {
		IntegerConfigKey integerConfigKey = new IntegerConfigKey(key);

		configKeys.add(integerConfigKey);

		return integerConfigKey;
	}

	public BooleanConfigKey createBooleanKey(String key) {
		BooleanConfigKey booleanConfigKey = new BooleanConfigKey(key);

		configKeys.add(booleanConfigKey);

		return booleanConfigKey;
	}

}
