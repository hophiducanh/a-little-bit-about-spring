package com.tellyouiam.alittlebitaboutspring.rest.velocity;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.tellyouiam.alittlebitaboutspring.service.velocity.VelocityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

@RestController
@RequestMapping("/velocity")
public class VelocityController {
	
	//https://github.com/sandeepbhardwaj/velocity-to-pdf-itext/blob/master/src/main/java/com/koko/itext/HtmlToPdf.java
	//https://stackoverflow.com/questions/25491379/runtimeworkerexception-invalid-nested-tag-head-found-expected-closing-tag-meta
	//https://quantrimang.com/html-va-xhtml-154158
	
	@Autowired
	private VelocityService velocitySv;
	
	@GetMapping
	public ResponseEntity<Object> testVelocity() {
		String html = velocitySv.testVelocitySyntax();
		PdfWriter pdfWriter;
		
		// create a new document
		Document document;
		try {
			document = new Document();
			// document header attributes
			document.addAuthor("Logbasex");
			document.addCreationDate();
			document.addProducer();
			document.addCreator("lobgasex.github.io");
			document.addTitle("HTML to PDF using itext");
			document.setPageSize(PageSize.LETTER);
			
			OutputStream file = new FileOutputStream(new File("/home/logbasex/Desktop/test.pdf"));
			pdfWriter = PdfWriter.getInstance(document, file);
			
			// open document
			document.open();
			
			XMLWorkerHelper xmlWorkerHelper = XMLWorkerHelper.getInstance();
			xmlWorkerHelper.getDefaultCssResolver(true);
//			xmlWorkerHelper.parseXHtml(pdfWriter, document, new StringReader(html));
			// close the document
//			document.close();
			// close the writer
//			pdfWriter.close();
			
			System.out.println("PDF generated successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(html);
	}
}
