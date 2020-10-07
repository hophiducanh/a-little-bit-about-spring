package com.tellyouiam.alittlebitaboutspring.utils.freemaker;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

public class PdfFileUtils {
	public static void savePdf(OutputStream out, String html) {
		Document document = new Document(PageSize.A4, 50, 50, 60, 60);
		try {
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(html));
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}
}
