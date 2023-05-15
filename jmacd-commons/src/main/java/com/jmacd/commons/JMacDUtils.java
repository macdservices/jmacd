package com.jmacd.commons;

import kong.unirest.GetRequest;
import kong.unirest.RequestBodyEntity;

public interface JMacDUtils extends JMacDCommons, JMacDCommonsErrors, HttpCommons, JMacDCommonsJson, JMacDFileIoUtils,
		CachedRowsetSerializationUtils, JMacDChecks {

	default GetRequest addToken(GetRequest getRequest, String token) {
		if (isNonEmpty(token) == true) {
			return getRequest.header("Authentication", "Bearer: " + token);
		} else {
			return getRequest;
		}
	}

	default RequestBodyEntity addToken(RequestBodyEntity postRequest, String token) {
		if (isNonEmpty(token) == true) {
			return postRequest.header("Authentication", "Bearer: " + token);
		} else {
			return postRequest;
		}
	}

}
