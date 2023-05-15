package com.jmacd.commons.dataObjects.queries;

import java.io.File;

import com.jmacd.commons.JMacDCommons;
import com.jmacd.commons.JMacDUtils;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryDefinition;
import com.jmacd.server.queryEngines.queryDefinitions.PromQueryRangeDefinition;
import com.jmacd.server.queryEngines.queryDefinitions.SqlQueryDefinition;

public class AddQueryBodyTest implements JMacDCommons {
	public static void main(String[] args) {
		JMacDUtils jMacDUtils = new JMacDUtils() {
		};

		AddPromqlQueryRangeBody addQueryBody0 //
				= AddPromqlQueryRangeBody.builder()//
						.queryName("Query-000")//
						.queryEngineName("QueryEngine-000")//
						.promQueryRangeDefinition(PromQueryRangeDefinition.builder()//
								.query("kube_pod_info")//
								.startTime(-60 * 10)//
								.endTime(0)//
								.step("1s")//
								.build())//
						.updateFrequencyInMilliseconds(1000)//
						.build();

		String body0 = jMacDUtils.getPrettyJson(addQueryBody0);

		System.out.println(body0);

		AddPromqlQueryBody addQueryBody1 //
				= AddPromqlQueryBody.builder()//
						.queryName("Query-001")//
						.queryEngineName("QueryEngine-001")//
						.promQueryDefinition(PromQueryDefinition.builder().query("kube_pod_info").build())//
						.updateFrequencyInMilliseconds(1000)//
						.build();

		String body1 = jMacDUtils.getPrettyJson(addQueryBody1);

		System.out.println(body1);

		jMacDUtils.writeFile(new File("misc/test-data/add-query-001.json"), body1);

		AddSqlQueryBody addQueryBody2 //
				= AddSqlQueryBody.builder()//
						.queryName("Query-002")//
						.queryEngineName("QueryEngine-002")//
						.sqlQueryDefinition(SqlQueryDefinition.builder().query("select * from table_01").build())//
						.updateFrequencyInMilliseconds(1000)//
						.build();

		String body2 = jMacDUtils.getPrettyJson(addQueryBody2);

		System.out.println(body2);

		jMacDUtils.writeFile(new File("misc/test-data/add-query-002.json"), body2);
	}
}
