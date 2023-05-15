package com.jmacd.commons.dataObjects.queries;

import com.jmacd.server.queryEngines.queryDefinitions.PromQueryDefinition;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddPromqlQueryBody extends AddQueryBody {

	private final PromQueryDefinition promQueryDefinition;

	/**
	 * @param queryName
	 * @param queryEngineName
	 * @param promQueryDefinition
	 * @param updateFrequencyInMilliseconds
	 */
	@Builder
	private AddPromqlQueryBody(String queryName, String queryEngineName, PromQueryDefinition promQueryDefinition,
			long updateFrequencyInMilliseconds) {
		super(queryName, queryEngineName, updateFrequencyInMilliseconds);

		this.promQueryDefinition = promQueryDefinition;
	}

}
