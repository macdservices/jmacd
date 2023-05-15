package com.jmacd.server;

import com.jmacd.commons.JMacDUtils;
import com.jmacd.commons.Results;
import com.jmacd.commons.dataObjects.AddQueryEngineBody;
import com.jmacd.commons.dataObjects.JMacDPathes;
import com.jmacd.commons.dataObjects.queries.AddPromqlQueryBody;
import com.jmacd.commons.dataObjects.queries.AddPromqlQueryRangeBody;
import com.jmacd.commons.dataObjects.queries.AddSqlQueryBody;
import com.jmacd.commons.dataObjects.queries.RemoveQueryBody;
import com.jmacd.commons.dataObjects.queries.RemoveQueryEngineBody;
import com.jmacd.server.queryEngines.QueryFactory;
import com.jmacd.server.queryEngines.QueryFactory.GetQueriesResults;
import com.jmacd.server.queryEngines.QueryFactory.GetQueryEnginesResults;
import com.jmacd.server.queryEngines.QueryFactory.GetQueuedRunnablesSizeResults;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryDefinition;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryRangeDefinition;
import com.jmacd.server.queryEngines.queryDefinitions.SqlQueryDefinition;

import io.jooby.Context;
import io.jooby.Jooby;
import io.jooby.Route.Handler;
import io.jooby.SSLHandler;
import io.jooby.ServerOptions;
import io.jooby.StatusCode;
import io.jooby.whoops.WhoopsModule;

public class JMacDServer extends Jooby implements JMacDUtils, JMacDPathes {

	private final QueryFactory queryFactory;

	public JMacDServer() {
		super();

		this.queryFactory = new QueryFactory(100);

		setServerOptions(new ServerOptions());
		getServerOptions().setSecurePort(8443);

		before(new SSLHandler("localhost"));

		install(new WhoopsModule());

		get("/", ctx -> "Welcome to JMacD!");

		get(GET_QUERY_ENGINES, getQueryEngines);

		get(GET_QUERIES, getQueryQueries);

		get(GET_QUEUED_RUNNABLES_SIZE, getQueuedRunnablesSize);

		get(GET_QUERY_STRING_VALUE, getQueryStringValue);

		get(GET_QUERY_ROWSET_VALUE, getQueryRowsetValue);

		post(ADD_SQL_QUERY, addSqlQuery);

		post(ADD_PROMQL_QUERY, addPromqlQuery);

		post(ADD_PROMQLRANGE_QUERY, addPromqlRangeQuery);

		post(ADD_QUERY_ENGINE, addQueryEngine);

		post(REMOVE_QUERY, removeQuery);

		post(REMOVE_QUERY_ENGINE, removeQueryEngine);

		openBrowser("https://localhost:8443");
	}

	@SuppressWarnings("serial")
	private Handler addQueryEngine //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					String body = ctx.body().value();

					AddQueryEngineBody addQueryEngineBody = fromJson(body, AddQueryEngineBody.class);

					if (addQueryEngineBody.getHikariConfig() != null) {
						queryFactory.//
								addQueryEngine(addQueryEngineBody.getQueryEngineName(), //
										addQueryEngineBody.getHikariConfig(), //
										addQueryEngineBody.getDbUserName(), //
										addQueryEngineBody.getDbUserPassword());

					} else {
						queryFactory.//
								addQueryEngine(addQueryEngineBody.getQueryEngineName(), //
										addQueryEngineBody.getQueryType(), //
										addQueryEngineBody.getQueryDatabaseUrl(), //
										addQueryEngineBody.getDbUserName(), //
										addQueryEngineBody.getDbUserPassword(), //
										addQueryEngineBody.getDbHttpBearerToken());
					}

					return "Added QueryEngine!";
				}
			};

	@SuppressWarnings("serial")
	private Handler addSqlQuery //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					String body = ctx.body().value();

					AddSqlQueryBody addSqlQueryBody = fromJson(body, AddSqlQueryBody.class);

					SqlQueryDefinition sqlQueryDefinition = addSqlQueryBody.getSqlQueryDefinition();

					queryFactory.//
							addQuery(addSqlQueryBody.getQueryName(), //
									addSqlQueryBody.getQueryEngineName(), //
									sqlQueryDefinition, //
									addSqlQueryBody.getUpdateFrequencyInMilliseconds());

					return "Added Query!";
				}
			};

	@SuppressWarnings("serial")
	private Handler addPromqlQuery //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					String body = ctx.body().value();

					AddPromqlQueryBody addPromqlQueryBody = fromJson(body, AddPromqlQueryBody.class);

					PromQueryDefinition promQueryDefinition = addPromqlQueryBody.getPromQueryDefinition();

					queryFactory.//
							addQuery(addPromqlQueryBody.getQueryName(), //
									addPromqlQueryBody.getQueryEngineName(), //
									promQueryDefinition, //
									addPromqlQueryBody.getUpdateFrequencyInMilliseconds());

					return "Added Query!";
				}
			};

	@SuppressWarnings("serial")
	private Handler addPromqlRangeQuery //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					String body = ctx.body().value();

					AddPromqlQueryRangeBody addPromqlQueryRangeBody = fromJson(body, AddPromqlQueryRangeBody.class);

					PromQueryRangeDefinition promQueryRangeDefinition = addPromqlQueryRangeBody
							.getPromQueryRangeDefinition();

					queryFactory.//
							addQuery(addPromqlQueryRangeBody.getQueryName(), //
									addPromqlQueryRangeBody.getQueryEngineName(), //
									promQueryRangeDefinition, //
									addPromqlQueryRangeBody.getUpdateFrequencyInMilliseconds());

					return "Added Query!";
				}
			};

	@SuppressWarnings("serial")
	private Handler getQueryStringValue //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					String queryName = ctx.query("query-name").value();

					Results<String> stringValue //
							= queryFactory.//
									getQueryStringResult(queryName);

					if (stringValue.isSucccessful() == false) {
						ctx.setResponseCode(StatusCode.NOT_FOUND);
					}

					return stringValue.getValue();
				}
			};

	@SuppressWarnings("serial")
	private Handler getQueryRowsetValue //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					String queryName = ctx.query("query-name").value();

					Results<byte[]> bytesValue //
							= queryFactory.//
									getQueryCachedRowSetBytesResult(queryName);

					if (bytesValue.isSucccessful() == false) {
						ctx.setResponseCode(StatusCode.NOT_FOUND);
					}

					return bytesValue.getValue();
				}
			};

/////////////			

	@SuppressWarnings("serial")
	private Handler getQueryQueries //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					GetQueriesResults getQueriesResults = queryFactory.getQueries();

					return toPrettyJson(getQueriesResults);
				}
			};

	@SuppressWarnings("serial")
	private Handler getQueuedRunnablesSize //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					GetQueuedRunnablesSizeResults getQueuedRunnablesSizeResults = queryFactory.getQueuedRunnablesSize();

					return toPrettyJson(getQueuedRunnablesSizeResults);
				}
			};

	@SuppressWarnings("serial")
	private Handler getQueryEngines //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					GetQueryEnginesResults getQueryEnginesResults = queryFactory.getQueryEngines();

					return toPrettyJson(getQueryEnginesResults);
				}
			};

	@SuppressWarnings("serial")
	private Handler removeQuery //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					String body = ctx.body().value();

					RemoveQueryBody removeQueryBody = fromJson(body, RemoveQueryBody.class);

					queryFactory.//
							removeQuery(removeQueryBody.getQueryName(), //
									removeQueryBody.getQueryEngineName());

					return "Removed Query!";
				}
			};

	@SuppressWarnings("serial")
	private Handler removeQueryEngine //
			= new Handler() {
				@Override
				public Object apply(Context ctx) throws Exception {
					String body = ctx.body().value();

					RemoveQueryEngineBody removeQueryEngineBody = fromJson(body, RemoveQueryEngineBody.class);

					queryFactory.//
							removeQueryEngine(removeQueryEngineBody.getQueryEngineName());

					return "Removed Query Engine!";
				}
			};

	public static void main(String[] args) {
		runApp(args, JMacDServer::new);
	}

}
