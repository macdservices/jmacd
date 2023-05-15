package com.jmacd.client;

import javax.sql.rowset.CachedRowSet;

import com.jmacd.commons.JMacDUtils;
import com.jmacd.commons.dataObjects.AddQueryEngineBody;
import com.jmacd.commons.dataObjects.JMacDPathes;
import com.jmacd.commons.dataObjects.QueryType;
import com.jmacd.commons.dataObjects.queries.AddPromqlQueryBody;
import com.jmacd.commons.dataObjects.queries.AddPromqlQueryRangeBody;
import com.jmacd.commons.dataObjects.queries.AddSqlQueryBody;
import com.jmacd.commons.dataObjects.queries.RemoveQueryBody;
import com.jmacd.commons.dataObjects.queries.RemoveQueryEngineBody;
import com.jmacd.commons.exceptions.NoResultsException;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryDefinition;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryRangeDefinition;
import com.jmacd.server.queryEngines.queryDefinitions.SqlQueryDefinition;
import com.zaxxer.hikari.HikariConfig;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class JMacDClient implements JMacDUtils, JMacDPathes {

	private final String url;
	private final String user;
	private final String password;
	private final String token;

	public JMacDClient(String url, String user, String password) {
		super();

		this.url = url;
		this.user = user;
		this.password = password;
		this.token = null;
	}

	public JMacDClient(String url, String token) {
		super();

		this.url = url;
		this.user = null;
		this.password = null;
		this.token = token;
	}

	public void addQueryEngine(//
			String queryEngineName, //
			QueryType queryType, //
			String queryDatabaseUrl, //
			String dbUserName, //
			String dbUserPassword, //
			String dbHttpBearerToken) {

		AddQueryEngineBody addQueryEngineBody //
				= AddQueryEngineBody.builder()//
						.queryEngineName(queryEngineName)//
						.queryType(queryType)//
						.queryDatabaseUrl(queryDatabaseUrl)//
						.dbUserName(dbUserName)//
						.dbUserPassword(dbUserPassword)//
						.dbHttpBearerToken(dbHttpBearerToken)//
						.hikariConfig(null)//
						.build();

		String result = postReturnString(ADD_QUERY_ENGINE, addQueryEngineBody);

		System.out.println(result);
	}

	public CachedRowSet getQueryCachedRowSetResults(String queryName) {
		HttpResponse<byte[]> result //
				= addToken(Unirest.get(url + GET_QUERY_ROWSET_VALUE)//
						.header("accept", "application/octet-stream")//
						.queryString("query-name", queryName), //
						token)//
						.asBytes();

		if (result.isSuccess() == true) {
			byte[] bytes = result.getBody();

			return getCachedRowSetFromBytes(bytes);
		} else {
			String errorObject = new String(result.getBody());

			throw new NoResultsException(errorObject);
		}

	}

	public String getPromqlQueryResults(String queryName) {
		HttpResponse<String> result //
				= addToken(Unirest.get(url + GET_QUERY_STRING_VALUE)//
						.header("accept", "application/json")//
						.queryString("query-name", queryName), //
						token)//
						.asString();

		if (result.isSuccess() == true) {
			String json = result.getBody();

			return json;
		} else {
			String errorObject = result.getBody();

			throw new NoResultsException(errorObject);
		}

	}

	public void addQueryEngine(//
			String queryEngineName, //
			HikariConfig hikariConfig, //
			String dbUserName, //
			String dbUserPassword) {

		AddQueryEngineBody addQueryEngineBody //
				= AddQueryEngineBody.builder()//
						.queryEngineName(queryEngineName)//
						.queryType(QueryType.Sql)//
						.queryDatabaseUrl(null)//
						.dbUserName(dbUserName)//
						.dbUserPassword(dbUserPassword)//
						.hikariConfig(hikariConfig)//
						.build();

		String result = postReturnString(ADD_QUERY_ENGINE, addQueryEngineBody);

		System.out.println(result);
	}

	public void addSqlQuery(//
			String queryName, //
			String queryEngineName, //
			String sqlQuery, //
			long updateFrequencyInMilliseconds) {

		SqlQueryDefinition sqlQueryDefinition//
				= SqlQueryDefinition.builder()//
						.query(sqlQuery)//
						.build();

		AddSqlQueryBody addSqlQueryBody //
				= AddSqlQueryBody.builder()//
						.queryName(queryName)//
						.queryEngineName(queryEngineName)//
						.sqlQueryDefinition(sqlQueryDefinition)//
						.updateFrequencyInMilliseconds(updateFrequencyInMilliseconds)//
						.build();

		String result = postReturnString(ADD_SQL_QUERY, addSqlQueryBody);

		System.out.println(result);
	}

	public void addPromqlQuery(//
			String queryName, //
			String queryEngineName, //
			String promQLquery, //
			long updateFrequencyInMilliseconds) {

		PromQueryDefinition promQueryDefinition//
				= PromQueryDefinition.builder()//
						.query(promQLquery)//
						.build();

		AddPromqlQueryBody addPromqlQueryBody //
				= AddPromqlQueryBody.builder()//
						.queryName(queryName)//
						.queryEngineName(queryEngineName)//
						.promQueryDefinition(promQueryDefinition)//
						.updateFrequencyInMilliseconds(updateFrequencyInMilliseconds)//
						.build();

		String result = postReturnString(ADD_PROMQL_QUERY, addPromqlQueryBody);

		System.out.println(result);
	}

	public void adddPromqlQueryRange(//
			String queryName, //
			String queryEngineName, //
			String promQLquery, //
			long startTime, //
			long endTime, //
			String step, //
			long updateFrequencyInMilliseconds) {

		PromQueryRangeDefinition promQueryRangeDefinition//
				= PromQueryRangeDefinition.builder()//
						.query(promQLquery)//
						.startTime(startTime)//
						.endTime(endTime)//
						.step(step)//
						.build();

		AddPromqlQueryRangeBody addPromqlQueryRangeBody //
				= AddPromqlQueryRangeBody.builder()//
						.queryName(queryName)//
						.queryEngineName(queryEngineName)//
						.promQueryRangeDefinition(promQueryRangeDefinition)//
						.updateFrequencyInMilliseconds(updateFrequencyInMilliseconds)//
						.build();

		String result = postReturnString(ADD_PROMQLRANGE_QUERY, addPromqlQueryRangeBody);

		System.out.println(result);
	}

	//////////////////////////

	public void getQueries() {
		HttpResponse<String> result //
				= addToken(Unirest.get(url + GET_QUERIES)//
						.header("accept", "application/json"), //
						token)//
						.asString();

		String json = result.getBody();

		System.out.println(json);
	}

	public void getQueryEngines() {
		HttpResponse<String> result //
				= addToken(Unirest.get(url + GET_QUERY_ENGINES)//
						.header("accept", "application/json"), //
						token)//
						.asString();

		String json = result.getBody();

		System.out.println(json);
	}

	public void getQueuedRunnablesSize() {
		HttpResponse<String> result //
				= addToken(Unirest.get(url + GET_QUEUED_RUNNABLES_SIZE)//
						.header("accept", "application/json"), //
						token)//
						.asString();

		String json = result.getBody();

		System.out.println(json);
	}

	public void removeQuery(String queryName, String queryEngineName) {
		RemoveQueryBody removeQueryBody//
				= RemoveQueryBody.builder()//
						.queryName(queryName)//
						.queryEngineName(queryEngineName)//
						.build();

		String bodyString = toPrettyJson(removeQueryBody);

		HttpResponse<String> result //
				= addToken(Unirest.post(url + REMOVE_QUERY)//
						.header("accept", "text/plain")//
						.body(bodyString), //
						token) //
						.asString();

		String resultString = result.getBody();

		System.out.println(resultString);
	}

	public void removeQueryEngine(String queryEngineName) {
		RemoveQueryEngineBody removeQueryEngineBody//
				= RemoveQueryEngineBody.builder()//
						.queryEngineName(queryEngineName)//
						.build();

		String bodyString = toPrettyJson(removeQueryEngineBody);

		HttpResponse<String> result //
				= addToken(Unirest.post(url + REMOVE_QUERY)//
						.header("accept", "text/plain")//
						.body(bodyString), //
						token) //
						.asString();

		String resultString = result.getBody();

		System.out.println(resultString);
	}

	//////////////////////////

	public <T> T postReturnJson(String path, Object object, Class<T> aClass) {
		String bodyString = toPrettyJson(object);

		HttpResponse<String> result //
				= addToken(Unirest.post(url + path)//
						.header("accept", "text/plain")//
						.body(bodyString), //
						token) //
						.asString();

		return fromJson(result.getBody(), aClass);
	}

	public String postReturnString(String path, Object object) {
		String bodyString = toPrettyJson(object);

		HttpResponse<String> result //
				= addToken(Unirest.post(url + path)//
						.header("accept", "text/plain")//
						.body(bodyString), //
						token) //
						.asString();

		return result.getBody();
	}

	public byte[] postReturnBinary(String path, Object object) {
		String bodyString = toPrettyJson(object);

		HttpResponse<byte[]> result //
				= addToken(Unirest.post(url + path)//
						.header("accept", "application/octet-stream")//
						.body(bodyString), //
						token) //
						.asBytes();

		return result.getBody();
	}

	public void setTrustCerts(boolean verifySsl) {
		Unirest.config().verifySsl(verifySsl);
	}
}
