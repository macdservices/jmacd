package com.jmacd.client;

import javax.sql.rowset.CachedRowSet;

import com.jmacd.commons.JMacDUtils;
import com.jmacd.commons.exceptions.NoResultsException;
import com.zaxxer.hikari.HikariConfig;

public class JMacDClientSqlTest extends TestingConfiguration implements JMacDUtils {

	private final JMacDClient jMacDClient;

	public JMacDClientSqlTest() {
		super("/jmacd-testing/jmacd-testing.conf");

		String url = "https://localhost:8443";
		String token = "TOKEN";

		jMacDClient = new JMacDClient(url, token);
		jMacDClient.setTrustCerts(false);
	}

	public void databaseQueryTest() {
		HikariConfig hikariConfig = new HikariConfig();

		hikariConfig.setJdbcUrl(jdbcUrlConfigKey.getValue(config));
		hikariConfig.setUsername(usernameConfigKey.getValue(config));
		hikariConfig.setPassword(passwordConfigKey.getValue(config));
		hikariConfig.addDataSourceProperty("cachePrepStmts", cachePrepStmtsConfigKey.getValue(config));
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSizeConfigKey.getValue(config));
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimitConfigKey.getValue(config));
		hikariConfig.addDataSourceProperty("sslMode", sslModeConfigKey.getValue(config));
		hikariConfig.addDataSourceProperty("verifyServerCertificate",
				verifyServerCertificateConfigKey.getValue(config)); // TODO to make true
		hikariConfig.addDataSourceProperty("useSSL", useSSLConfigKey.getValue(config));
		hikariConfig.addDataSourceProperty("requireSSL", requireSSLConfigKey.getValue(config));

		jMacDClient//
				.addQueryEngine("QueryEngine-002", //
						hikariConfig, //
						usernameConfigKey.getValue(config), //
						passwordConfigKey.getValue(config));

		String sqlString = ""//
				+ "SELECT" + "\n" //
				+ "  test_value_id, " + "\n" //
				+ "  test_value_type, " + "\n" //
				+ "  test_value, " + "\n" //
				+ "  update_user, " + "\n" //
				+ "  update_date" + "\n" //
				+ "FROM " + "\n" //
				+ "  testing.test_values" + "\n" //
				+ "";//

		jMacDClient//
				.addSqlQuery("Query-002", //
						"QueryEngine-002", //
						sqlString, //
						1000);

		jMacDClient//
				.addSqlQuery("Query-003", //
						"QueryEngine-002", //
						sqlString, //
						5000);

		jMacDClient.getQueryEngines();

		jMacDClient.getQueries();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		jMacDClient.getQueuedRunnablesSize();

		System.out.println("Removing Query-003");

		jMacDClient.removeQuery("Query-002", "QueryEngine-002");

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		jMacDClient.getQueries();

		jMacDClient.getQueuedRunnablesSize();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				long startTime = System.currentTimeMillis();
				CachedRowSet cachedRowSet //
						= jMacDClient//
								.getQueryCachedRowSetResults("Query-002");
				long endTime = System.currentTimeMillis();

				System.out.println("Query took " + (endTime - startTime) + " milliseconds");

				new JMacDUtils() {
				}.displayRowset(cachedRowSet);
			} catch (NoResultsException e1) {
				e1.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		JMacDClientSqlTest jMacDClientTest = new JMacDClientSqlTest();

		jMacDClientTest.databaseQueryTest();
	}
}
