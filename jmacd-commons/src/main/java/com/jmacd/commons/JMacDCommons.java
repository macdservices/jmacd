package com.jmacd.commons;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.rowset.CachedRowSet;

import lombok.SneakyThrows;

public interface JMacDCommons {

	default String quoted(String value) {
		return "\"" + value + "\""; //$NON-NLS-1$ //$NON-NLS-2$
	}

	default List<String> quoted(String[] values) {
		return quoted(toList(values));
	}

	default <T> List<T> toList(T[] values) {
		return Arrays.asList(values);
	}

	default List<String> quoted(List<String> values) {
		return values.stream().map(new Function<String, String>() {
			@Override
			public String apply(String string) {
				return quoted(string);
			}
		}).collect(Collectors.toList());
	}

	default Set<String> quoted(Set<String> values) {
		return values.stream().map(new Function<String, String>() {
			@Override
			public String apply(String string) {
				return quoted(string);
			}
		}).collect(Collectors.toSet());
	}

	default Set<String> quoted(Collection<String> values) {
		return values.stream().map(new Function<String, String>() {
			@Override
			public String apply(String string) {
				return quoted(string);
			}
		}).collect(Collectors.toSet());
	}

	default String singleQuoted(String value) {
		return "'" + value + "'"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	default String joinLines(List<String> lines) {
		return String.join("\n", lines); //$NON-NLS-1$
	}

	default String joinLines(String... lines) {
		return String.join("\n", lines); //$NON-NLS-1$
	}

	@SneakyThrows
	default void displayRowset(CachedRowSet cachedRowSet) {
		cachedRowSet.beforeFirst();

		while (cachedRowSet.next()) {

			System.out.print("{");
			for (int columnIndex = 0; columnIndex < cachedRowSet.getMetaData().getColumnCount(); columnIndex++) {
				String columnName = cachedRowSet.getMetaData().getColumnName(columnIndex + 1);
				Object value = cachedRowSet.getObject(columnName);

				System.out.print(columnName + ":" + value + ",");
			}
			System.out.println("}");
		}
	}

}
