package com.jmacd.commons.dataObjects.queries;

import com.jmacd.server.queryEngines.queryDefinitions.SqlQueryDefinition;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddSqlQueryBody extends AddQueryBody {

	private final SqlQueryDefinition sqlQueryDefinition;

	/**
	 * @param queryName
	 * @param queryEngineName
	 * @param sqlQueryDefinition
	 * @param updateFrequencyInMilliseconds
	 */
	@Builder
	private AddSqlQueryBody(String queryName, String queryEngineName, SqlQueryDefinition sqlQueryDefinition,
			long updateFrequencyInMilliseconds) {
		super(queryName, queryEngineName, updateFrequencyInMilliseconds);

		this.sqlQueryDefinition = sqlQueryDefinition;
	}
}
