package com.tellyouiam.alittlebitaboutspring.service.masterdata;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MasterDataCrawlServiceImpl implements MasterDataCrawlService {
	private static final String MASTER_DATA_LINK_URL = "https://www.prism.horse/onboarding?t=f&uuid=a0d7fef0-feb4-4d52-835c-9b31ca595fec&type=config";
	
	@Override
	public Object crawMasterData(String masterDataLink) throws IOException {
		//sample: https://www.prism.horse/onboarding?result=f&t=f&type=plan&uuid=ea3c8220-8e8a-49a6-a466-1ecba02db68b
		Document document = Jsoup.connect(MASTER_DATA_LINK_URL).get();
		
		Elements element = document.getElementsByTag("div");
		System.out.println(element);
		
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		//https://mkyong.com/java/itext-read-and-write-pdf-in-java/
		
		URL url = new URL(MASTER_DATA_LINK_URL);
		try (InputStream in = url.openStream()) {
			
			Files.copy(in,
					Paths.get("/home/logbasex/Desktop/head-first-javascript.22.pdf"),
					StandardCopyOption.REPLACE_EXISTING);
			
			//PdfReader reader = new PdfReader(filePath);
			PdfReader reader = new PdfReader("/home/logbasex/Desktop/head-first-javascript.22.pdf");
			
			// pageNumber = 1
			String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);
			System.out.println(textFromPage);
			
			String value = IntStream.range(1, reader.getNumberOfPages())
					.mapToObj(i -> {
						try {
							return PdfTextExtractor.getTextFromPage(reader, i);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					})
					.collect(Collectors.joining("\n"));
			System.out.println(value);
			
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
