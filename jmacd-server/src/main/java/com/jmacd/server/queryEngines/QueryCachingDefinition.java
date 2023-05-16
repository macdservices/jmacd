package com.jmacd.server.queryEngines;

import java.util.concurrent.ScheduledFuture;

import com.jmacd.server.queryEngines.queryDefinitions.QueryDefinition;

import lombok.Builder;
import lombok.Data;

@Data
public class QueryCachingDefinition<T, U extends QueryDefinition> {

	private final String queryName;
	private QueryEngine<T, U> queryEngine;
	private U queryDefinition;
	private long updateFrequencyInMilliseconds;
	transient private ScheduledFuture<?> scheduledFuture;

	@Builder
	public QueryCachingDefinition(//
			String queryName, //
			QueryEngine<T, U> queryEngine, //
			U queryDefinition, //
			long updateFrequencyInMilliseconds) {
		super();

		this.queryName = queryName;
		this.queryEngine = queryEngine;
		this.queryDefinition = queryDefinition;
		this.updateFrequencyInMilliseconds = updateFrequencyInMilliseconds;
	}

}
