package com.jmacd.server.queryEngines;

import java.net.URI;

import com.bdwise.prometheus.client.builder.InstantQueryBuilder;
import com.bdwise.prometheus.client.builder.QueryBuilderType;
import com.jmacd.commons.JMacDUtils;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryDefinition;

import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Builder;
import lombok.SneakyThrows;

public class PromqlQueryEngine extends QueryEngine<String, PromQueryDefinition> implements JMacDUtils {

	private final String dbHttpBearerToken;

	@Builder
	private PromqlQueryEngine(//
			String queryEngineName, //
			String queryDatabaseUrl, //
			String userName, //
			String userPassword, //
			String dbHttpBearerToken) {
		super(queryEngineName, queryDatabaseUrl, userName, userPassword);

		this.dbHttpBearerToken = dbHttpBearerToken;
	}

	@SneakyThrows
	@Override
	public String query(PromQueryDefinition promQueryDefinition) {
		InstantQueryBuilder iqb = QueryBuilderType.InstantQuery.newInstance(queryDatabaseUrl);
		URI targetUri = iqb.withQuery(promQueryDefinition.getQuery()).build();

		GetRequest getRequest //
				= Unirest.get(targetUri.toURL().toString())//
						.header("accept", "application/json");

		if ((isNonEmpty(userName) == true) && (isNonEmpty(userPassword) == true)) {
			getRequest = getRequest.basicAuth(userName, userPassword);
		}

		if (isNonEmpty(dbHttpBearerToken) == true) {
			getRequest = getRequest.header("Authentication", "Bearer " + dbHttpBearerToken);
		}

		HttpResponse<String> result = getRequest.asString();

		String promqlResults = result.getBody();

		return promqlResults;
	}

}
