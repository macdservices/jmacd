package com.jmacd.server.queryEngines;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

import lombok.Data;

@Data
public class MetricsManager {

	private final MetricRegistry metricsRegistry;
	private final HealthCheckRegistry healthChecksRegistry;

	public MetricsManager() {
		super();

		metricsRegistry = new MetricRegistry();
		healthChecksRegistry = new HealthCheckRegistry();
	}

}
