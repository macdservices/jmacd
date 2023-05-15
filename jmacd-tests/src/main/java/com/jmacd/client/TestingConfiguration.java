package com.jmacd.client;

import java.io.File;

import com.jmacd.commons.config.BooleanConfigKey;
import com.jmacd.commons.config.ConfigKeys;
import com.jmacd.commons.config.IntegerConfigKey;
import com.jmacd.commons.config.StringConfigKey;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import lombok.SneakyThrows;

public class TestingConfiguration {

	public static ConfigKeys configKeys = new ConfigKeys();

	public final static StringConfigKey jdbcUrlConfigKey //
			= configKeys.createStringKey("testConfig.postgres.hikariConfig.jdbcUrl");
	public final static StringConfigKey usernameConfigKey //
			= configKeys.createStringKey("testConfig.postgres.hikariConfig.username");
	public final static StringConfigKey passwordConfigKey //
			= configKeys.createStringKey("testConfig.postgres.hikariConfig.password");

	public final static BooleanConfigKey cachePrepStmtsConfigKey //
			= configKeys.createBooleanKey("testConfig.postgres.hikariConfig.dataSourceProperties.cachePrepStmts");
	public final static IntegerConfigKey prepStmtCacheSizeConfigKey //
			= configKeys.createIntegerKey("testConfig.postgres.hikariConfig.dataSourceProperties.prepStmtCacheSize");
	public final static IntegerConfigKey prepStmtCacheSqlLimitConfigKey //
			= configKeys
					.createIntegerKey("testConfig.postgres.hikariConfig.dataSourceProperties.prepStmtCacheSqlLimit");
	public final static StringConfigKey sslModeConfigKey //
			= configKeys.createStringKey("testConfig.postgres.hikariConfig.dataSourceProperties.sslMode");
	public final static BooleanConfigKey verifyServerCertificateConfigKey //
			= configKeys
					.createBooleanKey("testConfig.postgres.hikariConfig.dataSourceProperties.verifyServerCertificate");
	public final static BooleanConfigKey useSSLConfigKey //
			= configKeys.createBooleanKey("testConfig.postgres.hikariConfig.dataSourceProperties.useSSL");
	public final static BooleanConfigKey requireSSLConfigKey //
			= configKeys.createBooleanKey("testConfig.postgres.hikariConfig.dataSourceProperties.requireSSL");

	protected final Config config;

	@SneakyThrows
	public TestingConfiguration(String configFilePath) {
		super();

		File configFile = new File(configFilePath);

		if (configFile.exists() == false) {
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
		}

		this.config = ConfigFactory.parseFile(configFile).resolve();

		configKeys.verifyConfigKeys(configFilePath, config);
	}

}
