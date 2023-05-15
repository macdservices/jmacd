package com.jmacd.commons.dataObjects.queries;

import com.jmacd.server.queryEngines.queryDefinitions.PromQueryRangeDefinition;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddPromqlQueryRangeBody extends AddQueryBody {

	private final PromQueryRangeDefinition promQueryRangeDefinition;

	/**
	 * @param queryName
	 * @param queryEngineName
	 * @param promQueryRangeDefinition
	 * @param updateFrequencyInMilliseconds
	 */
	@Builder
	private AddPromqlQueryRangeBody(String queryName, String queryEngineName,
			PromQueryRangeDefinition promQueryRangeDefinition, long updateFrequencyInMilliseconds) {
		super(queryName, queryEngineName, updateFrequencyInMilliseconds);

		this.promQueryRangeDefinition = promQueryRangeDefinition;
	}

}