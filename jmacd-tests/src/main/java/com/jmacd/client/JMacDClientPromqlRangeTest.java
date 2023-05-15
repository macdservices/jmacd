package com.jmacd.client;

import com.jmacd.commons.JMacDUtils;
import com.jmacd.commons.dataObjects.QueryType;
import com.jmacd.commons.exceptions.NoResultsException;

public class JMacDClientPromqlRangeTest extends TestingConfiguration implements JMacDUtils {

	private final JMacDClient jMacDClient;

	public JMacDClientPromqlRangeTest() {
		super("/jmacd-testing/jmacd-testing.conf");

		String url = "https://localhost:8443";
		String token = "TOKEN";

		jMacDClient = new JMacDClient(url, token);
		jMacDClient.setTrustCerts(false);
	}

	public void promQLQueryTest() {
		jMacDClient//
				.addQueryEngine("QueryEngine-001", //
						QueryType.Promql, //
						"http://www.fulltiltgoods.com:9090/", //
						"aUser", //
						"aPassword", //
						"aToken");
		jMacDClient//
				.adddPromqlQueryRange("Query-003", //
						"QueryEngine-001", //
						"rate(node_cpu_seconds_total[60s])", //
						System.currentTimeMillis() / 1000 - 60 * 10, //
						System.currentTimeMillis() / 1000, //
						"60s", //
						1000);

		jMacDClient.getQueryEngines();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		jMacDClient.getQueries();

		jMacDClient.getQueuedRunnablesSize();

		while (true) {
			try {
				long startTime = System.currentTimeMillis();
				String json //
						= jMacDClient//
								.getPromqlQueryResults("Query-003");
				long endTime = System.currentTimeMillis();

				System.out.println("Query took " + (endTime - startTime) + " milliseconds");

				System.out.println(json);
			} catch (NoResultsException e1) {
				e1.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		JMacDClientPromqlRangeTest jMacDClientTest = new JMacDClientPromqlRangeTest();

		jMacDClientTest.promQLQueryTest();
	}
}
