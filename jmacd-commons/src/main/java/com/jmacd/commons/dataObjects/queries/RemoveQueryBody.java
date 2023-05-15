package com.jmacd.commons.dataObjects.queries;

import lombok.Builder;
import lombok.Data;

@Data
public class RemoveQueryBody {

	private final String queryName;
	private final String queryEngineName;

	/**
	 * @param queryName
	 * @param queryEngineName
	 */
	@Builder
	private RemoveQueryBody(String queryName, String queryEngineName) {
		super();

		this.queryName = queryName;
		this.queryEngineName = queryEngineName;
	}
}