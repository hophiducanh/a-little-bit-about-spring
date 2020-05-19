package com.tellyouiam.alittlebitaboutspring.service.masterdata;

import io.github.jonathanlink.PDFLayoutTextStripper;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LibraryLayoutStripper {
	public static void main(String[] args) {
		String string = null;
		try {
			PDFParser pdfParser = new PDFParser(new RandomAccessFile(
					new File("src/main/resources/pdf-files/onboarding.pdf"), "r"));
			pdfParser.parse();
			PDDocument pdDocument = new PDDocument(pdfParser.getDocument());
			PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper();
			string = pdfTextStripper.getText(pdDocument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(string);
	}
}
