package com.jmacd.server.queryEngines.queryDefinitions;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class SqlQueryDefinition extends QueryDefinition {

	private final String query;

}
