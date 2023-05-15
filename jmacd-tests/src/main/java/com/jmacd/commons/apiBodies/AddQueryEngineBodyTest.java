package com.jmacd.commons.apiBodies;

import java.io.File;

import com.jmacd.commons.JMacDCommons;
import com.jmacd.commons.JMacDUtils;
import com.jmacd.commons.dataObjects.AddQueryEngineBody;
import com.jmacd.commons.dataObjects.QueryType;

public class AddQueryEngineBodyTest implements JMacDCommons {
	public static void main(String[] args) {
		JMacDUtils jMacDUtils = new JMacDUtils() {
		};

		AddQueryEngineBody addQueryEngineBody1 //
				= AddQueryEngineBody.builder()//
						.queryEngineName("Engine-001")//
						.queryType(QueryType.Promql)//
						.queryDatabaseUrl("http://ssss")//
						.dbUserName("aUser")//
						.dbUserPassword("aPassword")//
						.build();

		String body1 = jMacDUtils.getPrettyJson(addQueryEngineBody1);

		System.out.println(body1);

		jMacDUtils.writeFile(new File("misc/test-data/add-query-engine-001.json"), body1);

		AddQueryEngineBody addQueryEngineBody2 //
				= AddQueryEngineBody.builder()//
						.queryEngineName("Engine-002")//
						.queryType(QueryType.Sql)//
						.queryDatabaseUrl("http://tttt")//
						.dbUserName("aUser2")//
						.dbUserPassword("aPassword2")//
						.build();

		String body2 = jMacDUtils.getPrettyJson(addQueryEngineBody2);

		System.out.println(body2);

		jMacDUtils.writeFile(new File("misc/test-data/add-query-engine-002.json"), body2);
	}
}
