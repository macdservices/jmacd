package com.jmacd.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.sql.rowset.CachedRowSet;

import lombok.SneakyThrows;

public interface CachedRowsetSerializationUtils {

	@SneakyThrows
	default byte[] getCachedRowSetBytes(CachedRowSet cachedRowSet) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);) {

			out.writeObject(cachedRowSet);

			out.close();

			byte barray[] = byteArrayOutputStream.toByteArray();

			return barray;
		}
	}

	@SneakyThrows
	default CachedRowSet getCachedRowSetFromBytes(byte barray[]) {
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(barray);
				ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);) {
			CachedRowSet cachedRowSetDeserialized = (CachedRowSet) objectInputStream.readObject();

			return cachedRowSetDeserialized;
		}
	}

}
