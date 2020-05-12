package com.tellyouiam.alittlebitaboutspring.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileHelper {
	//https://gist.github.com/tranminhan/991207
	//https://stackoverflow.com/questions/40283179/how-to-convert-xlsx-file-to-csv-using-java
	
	private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);
	
	private static final String WINDOW_OUTPUT_FILE_PATH = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\";
	private static final String UNIX_OUTPUT_FILE_PATH = "/home/logbasex/Desktop/data/";
	
	public static void writeDataToFile(String filepath, byte[] data) {
		try {
			Files.write(Paths.get(filepath), data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getOutputFolderPath() {
		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.contains("win")) {
			return WINDOW_OUTPUT_FILE_PATH;
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			return UNIX_OUTPUT_FILE_PATH;
		}
		return null;
	}
	
	public static String getOutputFolder(String dirName) {
		String initFolderPath = getOutputFolderPath();
		Path outputDirPath = Paths.get(Objects.requireNonNull(initFolderPath), dirName, "submit");
		
		Path path = null;
		boolean dirExists = Files.exists(outputDirPath);
		if (!dirExists) {
			try {
				path = Files.createDirectories(outputDirPath);
			} catch (IOException io) {
				logger.error("Error occur when create the folder at: {}", outputDirPath.toAbsolutePath().toString());
			}
		}
		return dirExists ? outputDirPath.toAbsolutePath().toString() : Objects.requireNonNull(path).toString();
	}
}
