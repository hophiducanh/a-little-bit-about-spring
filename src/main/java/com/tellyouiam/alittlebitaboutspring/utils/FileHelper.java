package com.tellyouiam.alittlebitaboutspring.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHelper {
	//https://gist.github.com/tranminhan/991207
	//https://stackoverflow.com/questions/40283179/how-to-convert-xlsx-file-to-csv-using-java
	
	public static void writeDataToFile(String filepath, byte[] data) {
		try {
			Files.write(Paths.get(filepath), data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
