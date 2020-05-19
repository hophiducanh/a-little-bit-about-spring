package com.tellyouiam.alittlebitaboutspring.service.masterdata;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.*;
import java.util.List;

public class GetCharLocationAndSize extends PDFTextStripper {
	//https://stackoverflow.com/questions/35937774/how-to-search-some-specific-string-or-a-word-and-there-coordinates-from-a-pdf-do
	public GetCharLocationAndSize() throws IOException {
	}
	
	/**
	 * @throws IOException If there is an error parsing the document.
	 */
	public static void main(String[] args) throws IOException {
		PDDocument document = null;
		String fileName = "src/main/resources/pdf-files/onboarding.pdf";
		try {
			document = PDDocument.load(new File(fileName));
			PDFTextStripper stripper = new GetCharLocationAndSize();
			stripper.setSortByPosition(true);
			stripper.setStartPage(0);
			stripper.setEndPage(document.getNumberOfPages());
			Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
			stripper.writeText(document, dummy);
		} finally {
			if (document != null) {
				document.close();
			}
		}
		
	}
	
	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */
	@Override
	protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
		for (TextPosition text : textPositions) {
			System.out.println(text.getUnicode() + " [(X=" + text.getXDirAdj() + ",Y=" +
					text.getYDirAdj() + ") height=" + text.getHeightDir() + " width=" +
					text.getWidthDirAdj() + "]");
		}
	}
}