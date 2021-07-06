package com.tellyouiam.alittlebitaboutspring.library.itext;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class GettingStarted {
	//http://tutorials.jenkov.com/java-itext/getting-started.html
	public static void main(String[] args) {
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream("HelloWorld.pdf"));
			
			document.open();
			document.add(new Paragraph("Hello, I am logbasex"));
			document.add(new Chunk("This is the sentence 1."));
			document.add(new Chunk("This is the sentence 2."));
			document.add(new Chunk("This is the sentence 3."));
			document.add(new Chunk("This is the sentence 4."));
			document.add(new Chunk("This is the sentence 5."));
			document.add(new Chunk("This is the sentence 6."));
			document.close();
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
	}
}
