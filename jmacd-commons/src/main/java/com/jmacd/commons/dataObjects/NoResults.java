package com.jmacd.commons.dataObjects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoResults {
	private final String resultType = "Error";
	private final String errorMessage;
}