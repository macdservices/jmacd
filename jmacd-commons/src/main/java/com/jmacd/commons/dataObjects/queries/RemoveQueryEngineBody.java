package com.jmacd.commons.dataObjects.queries;

import lombok.Builder;
import lombok.Data;

@Data
public class RemoveQueryEngineBody {

	private final String queryEngineName;

	/**
	 * @param queryName
	 * @param queryEngineName
	 */
	@Builder
	private RemoveQueryEngineBody(String queryEngineName) {
		super();

		this.queryEngineName = queryEngineName;
	}
}