package com.jmacd.server.queryEngines;

import com.jmacd.server.queryEngines.queryDefinitions.QueryDefinition;

import lombok.Data;

@Data
public abstract class QueryEngine<T, U extends QueryDefinition> {

	protected final String queryEngineName;
	protected String queryDatabaseUrl;
	protected String userName;
	protected String userPassword;

	protected QueryEngine(String queryEngineName, String queryDatabaseUrl, String userName, String userPassword) {
		super();

		this.queryEngineName = queryEngineName;
		this.queryDatabaseUrl = queryDatabaseUrl;
		this.userName = userName;
		this.userPassword = userPassword;
	}

	public abstract T query(U queryDefinition);

}