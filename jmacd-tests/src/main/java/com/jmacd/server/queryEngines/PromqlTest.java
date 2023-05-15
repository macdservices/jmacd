package com.jmacd.server.queryEngines;

import java.net.URI;

import com.bdwise.prometheus.client.builder.InstantQueryBuilder;
import com.bdwise.prometheus.client.builder.QueryBuilderType;
import com.bdwise.prometheus.client.builder.RangeQueryBuilder;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.SneakyThrows;

public class PromqlTest {

	@SneakyThrows
	public static void main(String[] args) {
		{
			InstantQueryBuilder iqb = QueryBuilderType.InstantQuery.newInstance("http://localhost:9090/");
			URI targetUri = iqb.withQuery("prometheus_http_requests_total[1m]").build();
			System.out.println(targetUri.toURL().toString());

			HttpResponse<String> result //
					= Unirest.get(targetUri.toURL().toString())//
							.header("accept", "application/json")//
							.asString();

			String promqlResults = result.getBody();

			System.out.println(promqlResults);
		}

		{
			RangeQueryBuilder rangeQueryBuilder = QueryBuilderType.RangeQuery.newInstance("http://localhost:9090/");
			URI targetUri //
					= rangeQueryBuilder.withQuery("irate(prometheus_http_requests_total[60s])")
							.withStartEpochTime(System.currentTimeMillis() / 1000 - 60 * 10) //
							.withEndEpochTime(System.currentTimeMillis() / 1000) //
							.withStepTime("60s").build();
			System.out.println(targetUri.toURL().toString());

			HttpResponse<String> result //
					= Unirest.get(targetUri.toURL().toString())//
							.header("accept", "application/json")//
							.asString();

			String promqlResults = result.getBody();

			System.out.println(promqlResults);
		}
	}
}
