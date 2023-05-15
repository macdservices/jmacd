package com.jmacd.server.queryEngines;

import java.net.URI;

import com.bdwise.prometheus.client.builder.QueryBuilderType;
import com.bdwise.prometheus.client.builder.RangeQueryBuilder;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryRangeDefinition;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Builder;
import lombok.SneakyThrows;

public class PromqlQueryRangeEngine extends QueryEngine<String, PromQueryRangeDefinition> {

	@Builder
	private PromqlQueryRangeEngine(String queryEngineName, String queryDatabaseUrl, String userName,
			String userPassword) {
		super(queryEngineName, queryDatabaseUrl, userName, userPassword);
	}

	@SneakyThrows
	@Override
	public String query(PromQueryRangeDefinition promQueryRangeDefinition) {
		RangeQueryBuilder rangeQueryBuilder = QueryBuilderType.RangeQuery.newInstance(queryDatabaseUrl);

		long startTime = promQueryRangeDefinition.getStartTime();
		if (startTime < 0) {
			startTime = System.currentTimeMillis() / 1000 - startTime;
		}

		long endTime = promQueryRangeDefinition.getStartTime();
		if (endTime <= 0) {
			endTime = System.currentTimeMillis() / 1000 - endTime;
		}

		String stepTime = promQueryRangeDefinition.getStep();

		URI targetUri //
				= rangeQueryBuilder.withQuery(promQueryRangeDefinition.getQuery()) //
						.withStartEpochTime(startTime) //
						.withEndEpochTime(endTime) //
						.withStepTime(stepTime) //
						.build();

		HttpResponse<String> result //
				= Unirest.get(targetUri.toURL().toString())//
						.header("accept", "application/json")//
						.asString();

		String promqlResults = result.getBody();

		return promqlResults;
	}

}
