package com.jmacd.server.queryEngines.queryDefinitions;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)

public class PromQueryRangeDefinition extends QueryDefinition {

	private final String query;

	private final long startTime;

	private final long endTime;

	private final String step;

}
