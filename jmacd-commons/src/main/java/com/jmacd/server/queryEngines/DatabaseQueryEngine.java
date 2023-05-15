package com.jmacd.server.queryEngines;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import com.jmacd.server.queryEngines.queryDefinitions.SqlQueryDefinition;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Builder;
import lombok.SneakyThrows;

public class DatabaseQueryEngine extends QueryEngine<CachedRowSet, SqlQueryDefinition> {

	private HikariDataSource hikariDataSource;

	@Builder
	public DatabaseQueryEngine(//
			String queryEngineName, //
			String jdbcUrl, //
			String userName, //
			String userPassword, //
			HikariConfig hikariConfig) {
		super(queryEngineName, jdbcUrl, userName, userPassword);

		if (hikariConfig == null) {
			int minimumIdle = 2;
			int maximumPoolSize = 10;
			hikariConfig = new HikariConfig();
			hikariConfig.setPoolName("svcPoolName");
			hikariConfig.setMaximumPoolSize(maximumPoolSize);
			hikariConfig.setMinimumIdle(minimumIdle);
			hikariConfig.setJdbcUrl(jdbcUrl);
		}

		hikariConfig.setUsername(userName);
		hikariConfig.setPassword(userPassword);

		MetricsManager metricsManager = new MetricsManager();

		hikariConfig.setHealthCheckRegistry(metricsManager.getHealthChecksRegistry());
		hikariConfig.setMetricRegistry(metricsManager.getMetricsRegistry());

		this.hikariDataSource = new HikariDataSource(hikariConfig);
	}

	@SneakyThrows
	@Override
	public CachedRowSet query(SqlQueryDefinition sqlQueryDefinition) {
		CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();

		try (Connection connection = hikariDataSource.getConnection();
				Statement statement = connection.createStatement();) {
			ResultSet resultSet = statement.executeQuery(sqlQueryDefinition.getQuery());

			cachedRowSet.populate(resultSet);

			return cachedRowSet;
		}
	}

}
