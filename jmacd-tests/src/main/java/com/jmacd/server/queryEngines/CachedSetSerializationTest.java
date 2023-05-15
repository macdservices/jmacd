package com.jmacd.server.queryEngines;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import com.jmacd.client.TestingConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

public class CachedSetSerializationTest extends TestingConfiguration {

	private final DataSource dataSource;

	public CachedSetSerializationTest() {
		super("/jmacd-testing/jmacd-testing.conf");

		this.dataSource = createDataSource();
	}

	protected DataSource createDataSource() {
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

		HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

		return hikariDataSource;
	}

	@SneakyThrows
	public CachedRowSet query(String sql) {
		CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();

		ResultSet resultSet = dataSource.getConnection().createStatement().executeQuery(sql);

		cachedRowSet.populate(resultSet);

		return cachedRowSet;
	}

	@Data
	@Builder
	public static class TestValue {
		private int test_value_id;
		private String test_value_type;
		private String test_value;

		private String update_user;
		private Timestamp update_date;
	}

//	static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

	@SneakyThrows
	public void runTest() {
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

		CachedRowSet cachedRowSet = query(sqlString);

		displayRowset(cachedRowSet);

		cachedRowSet.beforeFirst();

//		FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
//
//		byte barray[] = conf.asByteArray(cachedRowSet);
//
//		CachedRowSet cachedRowSetDeserialized = (CachedRowSet) conf.asObject(barray);
//
//		displayRowset(cachedRowSetDeserialized);

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);) {

			out.writeObject(cachedRowSet);

			out.close();

			byte barray[] = byteArrayOutputStream.toByteArray();

			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(barray);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

			CachedRowSet cachedRowSetDeserialized = (CachedRowSet) objectInputStream.readObject();

			displayRowset(cachedRowSetDeserialized);
		}
	}

	private void displayRowset(CachedRowSet cachedRowSet) throws SQLException {
		while (cachedRowSet.next()) {
			int test_value_id = cachedRowSet.getInt("test_value_id");
			String test_value_type = cachedRowSet.getString("test_value_type");
			String test_value = cachedRowSet.getString("test_value");

			String update_user = cachedRowSet.getString("update_user");
			Timestamp update_date = cachedRowSet.getTimestamp("update_date");

			TestValue testValue//
					= TestValue.builder()//
							.test_value_id(test_value_id)//
							.test_value_type(test_value_type)//
							.test_value(test_value)//
							.update_user(update_user)//
							.update_date(update_date)//
							.build();

			System.out.println(testValue);
		}
	}

	public static void main(String[] args) {
		CachedSetSerializationTest cachedSetSerializationTest = new CachedSetSerializationTest();

		cachedSetSerializationTest.runTest();
	}

}
