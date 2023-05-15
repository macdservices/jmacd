package com.jmacd.commons;

public interface PropertyEncryptionProvider {

	String encryptProperty(String property);

	String decryptProperty(String property);

}
