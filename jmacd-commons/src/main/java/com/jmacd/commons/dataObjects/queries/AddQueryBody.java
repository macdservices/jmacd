package com.jmacd.commons.dataObjects.queries;

import lombok.Data;

@Data
public abstract class AddQueryBody {

	protected final String queryName;
	protected final String queryEngineName;
	protected final long updateFrequencyInMilliseconds;

	/**
	 * @param queryName
	 * @param queryEngineName
	 * @param updateFrequencyInMilliseconds
	 */
	protected AddQueryBody(String queryName, String queryEngineName, long updateFrequencyInMilliseconds) {
		super();

		this.queryName = queryName;
		this.queryEngineName = queryEngineName;
		this.updateFrequencyInMilliseconds = updateFrequencyInMilliseconds;
	}
}