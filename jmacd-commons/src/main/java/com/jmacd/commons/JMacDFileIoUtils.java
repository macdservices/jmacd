package com.jmacd.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public interface JMacDFileIoUtils extends JMacDCommonsJson, JMacDCommons {

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	default byte[] readClassFileToBytes(Class<?> aClass, String fileName) {
		try {
			InputStream fileInputStream = aClass.getResourceAsStream(fileName);

			if (fileInputStream == null) {
				throw new FileNotFoundException("class://" + fileName + " relative to " + aClass.getName()); //$NON-NLS-1$ //$NON-NLS-2$
			}

			return IOUtils.toByteArray(fileInputStream);
		} catch (IOException e) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_3, //
					"Failed to read file " + quoted(fileName) + " from class path starting at class, " //$NON-NLS-1$ //$NON-NLS-2$
							+ quoted(aClass.getName()),
					e);
		}
	}

	@SuppressWarnings("deprecation")
	default String readClassFileToString(Class<?> aClass, String fileName) {
		try {
			InputStream fileInputStream = aClass.getResourceAsStream(fileName);

			if (fileInputStream == null) {
				throw new FileNotFoundException("class://" + fileName + " relative to " + aClass.getName()); //$NON-NLS-1$ //$NON-NLS-2$
			}

			return IOUtils.toString(fileInputStream);
		} catch (IOException e) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_4, //
					"Failed to read file " + quoted(fileName) + " from class path starting at class, " //$NON-NLS-1$ //$NON-NLS-2$
							+ quoted(aClass.getName()),
					e);
		}
	}

	default File writeLinesToFile(String fileName, List<String> linesList) {
		File file = new File(fileName);

		writeLinesToFile(file, linesList);

		return file;
	}

	default void writeLinesToFile(File file, List<String> linesList) {
		String linesString = joinLines(linesList);

		writeFile(file, linesString);
	}

	default void writeJsonFile(File file, Object object) {
		writeFile(file, toJson(object));
	}

	default void writeJsonFile(String fileName, Object object) {
		writeFile(fileName, toJson(object));
	}

	default Object readFromJsonFile(File file, Class<?> aClass) {
		String jsonString = readFileToString(file);

		return fromJson(jsonString, aClass);
	}

	default Object readFromJsonFile(String fileName, Class<?> aClass) {
		String jsonString = readFileToString(fileName);

		return fromJson(jsonString, aClass);
	}

	default void writeFile(String fileName, CharSequence data) {
		File file = new File(fileName);

		writeFile(file, data);
	}

	@SuppressWarnings("deprecation")
	default void writeFile(File file, CharSequence data) {
		if (file.exists() == true) {
			if (file.canWrite() == false) {
				throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_5, //
						"writeFile(): Do not have permissions to write to existing file, \"" + file.getAbsolutePath() //$NON-NLS-1$
								+ "\""); //$NON-NLS-1$
			}
		}

		try {
			FileUtils.write(file.getAbsoluteFile(), data);
		} catch (IOException e) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_6, //
					"Could not write file, \"" + file.getAbsolutePath() + "\"", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	default byte[] readFileToBytes(String fileName) {
		File file = new File(fileName);

		return readFileToBytes(file);
	}

	default byte[] readFileToBytes(File file) {

		commonFileReadAssertions(file);

		try {
			return FileUtils.readFileToByteArray(file.getAbsoluteFile());
		} catch (IOException e) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_7, //
					"readFileToBytes(): Could not read file, \"" + file.getAbsolutePath() + "\"", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	default String readFileToString(String fileName) {
		File file = new File(fileName);

		return readFileToString(file);
	}

	private static void commonFileReadAssertions(File file) {
		if (file.getAbsoluteFile().exists() == false) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_8, //
					"readFileToString(): File does not exist, \"" + file.getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (file.getAbsoluteFile().canRead() == false) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_9, //
					"readFileToString(): Do not have permissions to read file, \"" + file.getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@SuppressWarnings("deprecation")
	default String readFileToString(File file) {

		commonFileReadAssertions(file);

		try {
			return FileUtils.readFileToString(file.getAbsoluteFile());
		} catch (IOException e) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_10, //
					"readFileToString(): Could not read file, \"" + file.getAbsolutePath() + "\"", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	default String readFilesToString(List<File> files) {
		return readFilesToString(EMPTY_STRING, files);
	}

	default String readFilesToString(File... files) {
		return readFilesToString(EMPTY_STRING, files);
	}

	default String readFilesToString(String separator, File... files) {
		return readFilesToString(separator, toList(files));
	}

	default String readFilesToString(String separator, List<File> files) {
		List<String> fileContentsList = new ArrayList<String>();

		for (File file : files) {
			fileContentsList.add(readFileToString(file));
		}

		return String.join(separator, fileContentsList);
	}

	default List<String> readFileToLines(String fileName) {
		File file = new File(fileName);

		return readFileToLines(file);
	}

	@SuppressWarnings("deprecation")
	default List<String> readFileToLines(File file) {

		if (file.exists() == false) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_11, //
					"readFileToLines(): File does not exist, \"" + file.getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (file.canRead() == false) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_12, //
					"readFileToLines(): Do not have permissions to read file, \"" + file.getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try {
			return FileUtils.readLines(file.getAbsoluteFile());
		} catch (IOException e) {
			throw new JMacDCommonsRuntimeException(JMacDCommonsErrors.JMacDCommonsError_ERROR_13, //
					"readFileToLines(): Could not read file, \"" + file.getAbsolutePath() + "\"", //$NON-NLS-1$ //$NON-NLS-2$
					e);
		}
	}

	default String getFileExtension(File file) {
		return getFileExtension(file.getAbsolutePath());
	}

	default String getFileExtension(String filePath) {
		return FilenameUtils.getExtension(filePath);
	}

}
