package com.jmacd.commons.dataObjects;

import com.zaxxer.hikari.HikariConfig;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddQueryEngineBody {
	private final String queryEngineName;
	private final QueryType queryType;
	private final String queryDatabaseUrl;
	private final String dbUserName;
	private final String dbUserPassword;
	private final String dbHttpBearerToken;
	private final HikariConfig hikariConfig;
}