package com.jmacd.server.queryEngines;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sql.rowset.CachedRowSet;

import com.bdwise.prometheus.client.builder.InstantQueryBuilder;
import com.bdwise.prometheus.client.builder.QueryBuilderType;
import com.bdwise.prometheus.client.builder.RangeQueryBuilder;
import com.jmacd.commons.JMacDUtils;
import com.jmacd.commons.Results;
import com.jmacd.commons.dataObjects.NoResults;
import com.jmacd.commons.dataObjects.QueryType;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryDefinition;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryRangeDefinition;
import com.jmacd.server.queryEngines.queryDefinitions.QueryDefinition;
import com.jmacd.server.queryEngines.queryDefinitions.SqlQueryDefinition;
import com.zaxxer.hikari.HikariConfig;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Builder;
import lombok.Data;

public class QueryFactory implements JMacDUtils {

	private Map<String, QueryEngine<?, ?>> nameToQueryEngineMap = new HashMap<>();

	private Map<String, QueryCachingDefinition<?, ?>> nameToQueryDefinitionMap = new HashMap<>();

	private Map<String, Object> queryNameToResultMap = new HashMap<>();

	private final ScheduledThreadPoolExecutor scheduledExecutorService;

	public QueryFactory(int poolSize) {
		super();

		this.scheduledExecutorService = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(poolSize);
	}

	public void shutdown() {
		scheduledExecutorService.shutdownNow();
	}

	public void addQueryEngine(//
			String queryEngineName, //
			HikariConfig hikariConfig, //
			String userName, //
			String userPassword) {

		synchronized (nameToQueryEngineMap) {
			nameToQueryEngineMap.put(queryEngineName, //
					DatabaseQueryEngine.builder()//
							.queryEngineName(queryEngineName)//
							.jdbcUrl(null) //
							.userName(userName)//
							.userPassword(userPassword)//
							.hikariConfig(hikariConfig)//
							.build());
		}
	}

	public void addQueryEngine(//
			String queryEngineName, //
			QueryType queryType, //
			String queryDatabaseUrl, //
			String userName, //
			String userPassword, //
			String dbHttpBearerToken) {
		switch (queryType) {
		case Promql: {

			nameToQueryEngineMap.put(queryEngineName, //
					PromqlQueryEngine.builder()//
							.queryEngineName(queryEngineName)//
							.queryDatabaseUrl(queryDatabaseUrl) //
							.userName(userName)//
							.userPassword(userPassword)//
							.dbHttpBearerToken(dbHttpBearerToken)//
							.build());
			break;
		}
		case Sql: {

			nameToQueryEngineMap.put(queryEngineName, //
					DatabaseQueryEngine.builder()//
							.queryEngineName(queryEngineName)//
							.jdbcUrl(queryDatabaseUrl) //
							.userName(userName)//
							.userPassword(userPassword)//
							.build());
			break;
		}
		case NoSql: {
			throw new RuntimeException("NoSql option not implemented yet - please help!");
		}
		default: {
			throw new IllegalArgumentException("Unexpected value: " + queryType);
		}
		}
	}

	public <T> T getQueryResult(String queryName, Class<T> aClass) {
		synchronized (queryNameToResultMap) {
			return (T) queryNameToResultMap.get(queryName);
		}
	}

	public void addQuery(//
			String queryName, //
			String queryEngineName, //
			QueryDefinition queryDefinition, //
			long updateFrequencyInMilliseconds) {
		if (nameToQueryEngineMap.containsKey(queryEngineName) == false) {
			throw new RuntimeException(
					"QueryEngine named, QUERY_ENGINE, was not defined.  Current engines are as follows: \nENGINE_LIST" //
							.replace("QUERY_ENGINE", queryEngineName) //
							.replace("ENGINE_LIST", String.join("\n", nameToQueryEngineMap.keySet())) //
			);
		}

		if (queryDefinition instanceof SqlQueryDefinition) {
			QueryEngine<CachedRowSet, SqlQueryDefinition> queryEngine //
					= (QueryEngine<CachedRowSet, SqlQueryDefinition>) nameToQueryEngineMap.get(queryEngineName);

			SqlQueryDefinition sqlQueryDefinition = (SqlQueryDefinition) queryDefinition;

			addSqlQuery(queryName, updateFrequencyInMilliseconds, queryEngine, sqlQueryDefinition);

		} else if (queryDefinition instanceof PromQueryDefinition) {
			QueryEngine<String, PromQueryDefinition> queryEngine //
					= (QueryEngine<String, PromQueryDefinition>) nameToQueryEngineMap.get(queryEngineName);

			PromQueryDefinition promQueryDefinition = (PromQueryDefinition) queryDefinition;

			addPromqlQuery(queryName, updateFrequencyInMilliseconds, queryEngine, promQueryDefinition);

		} else if (queryDefinition instanceof PromQueryRangeDefinition) {
			QueryEngine<String, PromQueryRangeDefinition> queryEngine //
					= (QueryEngine<String, PromQueryRangeDefinition>) nameToQueryEngineMap.get(queryEngineName);

			PromQueryRangeDefinition promQueryRangeDefinition = (PromQueryRangeDefinition) queryDefinition;

			addPromqlRangeQuery(queryName, updateFrequencyInMilliseconds, queryEngine, promQueryRangeDefinition);

		} else {
			throw new IllegalArgumentException("Unexpected value: " + queryDefinition);
		}
	}

	@Data
	public static abstract class QueryCachingDefinitionRunnable implements Runnable {
		private final QueryCachingDefinition<?, ?> queryCachingDefinition;

		/**
		 * @param queryCachingDefinition
		 */
		public QueryCachingDefinitionRunnable(QueryCachingDefinition<?, ?> queryCachingDefinition) {
			super();
			this.queryCachingDefinition = queryCachingDefinition;
		}
	}

	private void addPromqlRangeQuery(//
			String queryName, //
			long updateFrequencyInMilliseconds, //
			QueryEngine<String, PromQueryRangeDefinition> queryEngine, //
			PromQueryRangeDefinition promQueryRangeDefinition) {

		QueryCachingDefinition<String, PromQueryRangeDefinition> queryCachingDefinition //
				= QueryCachingDefinition.<String, PromQueryRangeDefinition>builder()//
						.queryName(queryName)//
						.queryEngine(queryEngine)//
						.queryDefinition(promQueryRangeDefinition)//
						.updateFrequencyInMilliseconds(updateFrequencyInMilliseconds).build();

		nameToQueryDefinitionMap.put(queryName, queryCachingDefinition);

		ScheduledFuture<?> scheduledFuture //
				= scheduledExecutorService.scheduleWithFixedDelay(//
						new QueryCachingDefinitionRunnable(queryCachingDefinition) {
							@Override
							public void run() {
								try {
//									System.out.println("Running " + promQueryRangeDefinition);
									RangeQueryBuilder rangeQueryBuilder //
											= QueryBuilderType.RangeQuery.newInstance(
													queryCachingDefinition.getQueryEngine().getQueryDatabaseUrl());
									URI targetUri //
											= rangeQueryBuilder
													.withQuery(queryCachingDefinition.getQueryDefinition().getQuery())
													.withStartEpochTime(
															queryCachingDefinition.getQueryDefinition().getStartTime()) //
													.withEndEpochTime(
															queryCachingDefinition.getQueryDefinition().getEndTime()) //
													.withStepTime(queryCachingDefinition.getQueryDefinition().getStep())
													.build();
//									System.out.println(targetUri.toURL().toString());

									HttpResponse<String> result //
											= Unirest.get(targetUri.toURL().toString())//
													.header("accept", "application/json")//
													.asString();

									String promqlResults = result.getBody();

									synchronized (queryNameToResultMap) {
										queryNameToResultMap.put(queryCachingDefinition.getQueryName(), promqlResults);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, 0, updateFrequencyInMilliseconds, TimeUnit.MILLISECONDS);

		queryCachingDefinition.setScheduledFuture(scheduledFuture);
	}

	private void addPromqlQuery(String queryName, long updateFrequencyInMilliseconds,
			QueryEngine<String, PromQueryDefinition> queryEngine, PromQueryDefinition promQueryDefinition) {

		QueryCachingDefinition<String, PromQueryDefinition> queryCachingDefinition //
				= QueryCachingDefinition.<String, PromQueryDefinition>builder()//
						.queryName(queryName)//
						.queryEngine(queryEngine)//
						.queryDefinition(promQueryDefinition)//
						.updateFrequencyInMilliseconds(updateFrequencyInMilliseconds).build();

		nameToQueryDefinitionMap.put(queryName, queryCachingDefinition);

		ScheduledFuture<?> scheduledFuture //
				= scheduledExecutorService.scheduleWithFixedDelay(//
						new QueryCachingDefinitionRunnable(queryCachingDefinition) {
							@Override
							public void run() {
								try {
//									System.out.println("Running " + promQueryDefinition);
									InstantQueryBuilder iqb //
											= QueryBuilderType.InstantQuery.newInstance(
													queryCachingDefinition.getQueryEngine().getQueryDatabaseUrl());

									URI targetUri = iqb.withQuery("prometheus_http_requests_total[1m]").build();

//									System.out.println(targetUri.toURL().toString());

									HttpResponse<String> result //
											= Unirest.get(targetUri.toURL().toString())//
													.header("accept", "application/json")//
													.asString();

									String promqlResults = result.getBody();

									synchronized (queryNameToResultMap) {
										queryNameToResultMap.put(queryCachingDefinition.getQueryName(), promqlResults);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, 0, updateFrequencyInMilliseconds, TimeUnit.MILLISECONDS);

		queryCachingDefinition.setScheduledFuture(scheduledFuture);
	}

	private void addSqlQuery(String queryName, long updateFrequencyInMilliseconds,
			QueryEngine<CachedRowSet, SqlQueryDefinition> queryEngine, SqlQueryDefinition sqlQueryDefinition) {
		QueryCachingDefinition<CachedRowSet, SqlQueryDefinition> queryCachingDefinition //
				= QueryCachingDefinition.<CachedRowSet, SqlQueryDefinition>builder()//
						.queryName(queryName)//
						.queryEngine(queryEngine)//
						.queryDefinition(sqlQueryDefinition)//
						.updateFrequencyInMilliseconds(updateFrequencyInMilliseconds).build();

		nameToQueryDefinitionMap.put(queryName, queryCachingDefinition);

		ScheduledFuture<?> scheduledFuture //
				= scheduledExecutorService.scheduleWithFixedDelay(//
						new QueryCachingDefinitionRunnable(queryCachingDefinition) {
							@Override
							public void run() {
								try {
									CachedRowSet cachedRowSet = queryCachingDefinition.getQueryEngine()
											.query(sqlQueryDefinition);

									synchronized (queryNameToResultMap) {
										queryNameToResultMap.put(queryCachingDefinition.getQueryName(),
												getCachedRowSetBytes(cachedRowSet));
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, 0, updateFrequencyInMilliseconds, TimeUnit.MILLISECONDS);

		queryCachingDefinition.setScheduledFuture(scheduledFuture);
	}

	public Results<String> getQueryStringResult(String queryName) {
		if (queryNameToResultMap.get(queryName) == null) {
			return Results.<String>builder()//
					.succcessful(false)//
					.value(toPrettyJson(NoResults.builder()//
							.errorMessage("No results for query: " + queryName)//
							.build()))//
					.build();
		}

		synchronized (queryNameToResultMap) {
			String resultString = (String) queryNameToResultMap.get(queryName);

			return Results.<String>builder()//
					.succcessful(true)//
					.value(resultString)//
					.build();
		}
	}

	public Results<byte[]> getQueryCachedRowSetBytesResult(String queryName) {
		if (queryNameToResultMap.get(queryName) == null) {
			return Results.<byte[]>builder()//
					.succcessful(false)//
					.value(toPrettyJson(NoResults.builder()//
							.errorMessage("No results for query: " + queryName)//
							.build()).getBytes())//
					.build();
		}

		synchronized (queryNameToResultMap) {
			byte[] cachedRowSetBytes = (byte[]) queryNameToResultMap.get(queryName);

			return Results.<byte[]>builder()//
					.succcessful(true)//
					.value(cachedRowSetBytes)//
					.build();
		}
	}

	@Data
	@Builder
	public static class GetQueryEnginesResults {
		private final List<QueryEngine<?, ?>> engines;
	}

	public GetQueryEnginesResults getQueryEngines() {
		synchronized (nameToQueryEngineMap) {
			return GetQueryEnginesResults.builder()//
					.engines(new ArrayList<>(nameToQueryEngineMap.values()))//
					.build();
		}
	}

	@Data
	@Builder
	public static class GetQueriesResults {
		private final List<QueryCachingDefinition<?, ?>> queryCachingDefinitions;
	}

	public GetQueriesResults getQueries() {
		synchronized (nameToQueryEngineMap) {
			return GetQueriesResults.builder()//
					.queryCachingDefinitions(new ArrayList<>(nameToQueryDefinitionMap.values()))//
					.build();
		}
	}

	public void removeQuery(String queryName, String queryEngineName) {
		if (nameToQueryDefinitionMap.containsKey(queryName) == false) {
			throw new IllegalArgumentException("No query named, " + queryName);
		}

		String definedQueryEngineName = nameToQueryDefinitionMap.get(queryName).getQueryEngine().getQueryEngineName();

		if (definedQueryEngineName.equals(queryEngineName) == false) {
			throw new IllegalArgumentException("QueryEngine name is, " + queryEngineName
					+ ", but the query's QueryEngine is " + definedQueryEngineName);
		}

		QueryCachingDefinition<?, ?> queryCachingDefinition = nameToQueryDefinitionMap.remove(queryName);

		ScheduledFuture<?> scheduledFuture = queryCachingDefinition.getScheduledFuture();

		System.out.println("Cancelling...");
		scheduledFuture.cancel(true);
		System.out.println("Cancelled");
	}

	public void removeQueryEngine(String queryEngineName) {
		if (nameToQueryEngineMap.containsKey(queryEngineName) == false) {
			throw new IllegalArgumentException("No query engine named, " + queryEngineName);
		}

		QueryEngine<?, ?> queryEngine = nameToQueryEngineMap.get(queryEngineName);

	}

	///

	@Data
	@Builder
	public static class GetQueuedRunnablesSizeResults {
		private final int size;
	}

	public GetQueuedRunnablesSizeResults getQueuedRunnablesSize() {

		GetQueuedRunnablesSizeResults getQueuedRunnablesResults //
				= GetQueuedRunnablesSizeResults.builder()//
						.size(scheduledExecutorService.getQueue().size())//
						.build();

		return getQueuedRunnablesResults;
	}
}
