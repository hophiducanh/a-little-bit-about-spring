package com.tellyouiam.alittlebitaboutspring.service.masterdata;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;
import com.tellyouiam.alittlebitaboutspring.utils.LambdaExceptionHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tellyouiam.alittlebitaboutspring.utils.LambdaExceptionHelper.*;

@Service
public class MasterDataCrawlServiceImpl implements MasterDataCrawlService {
	private static final String MASTER_DATA_LINK_URL = "https://www.prism.horse/onboarding?t=f&uuid=a0d7fef0-feb4-4d52-835c-9b31ca595fec&type=config";
	//https://www.prism.horse/onboarding?t=f&type=config&uuid=f62d3a3b-69f0-40c3-935c-8535f54b4467
	
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
		
		String filePath = "src/main/resources/pdf-files/onboarding.pdf";
		try {
			//PdfReader reader = new PdfReader(filePath);
			//TODO new URL()
			PdfReader reader = new PdfReader(filePath);
			
			Rectangle rect = new Rectangle(50, 50, 612, 750);
			RenderFilter filter = new RegionTextRenderFilter(rect);
			//trying to ignore header footer while reading pdf file
			TextExtractionStrategy strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter);
			
			String pageOne = PdfTextExtractor.getTextFromPage(reader, 1, strategy);
			//System.out.println(pageOne);
			
			float left = reader.getPageSize(1).getLeft();
			float right = reader.getPageSize(1).getRight();//612
			float bottom = reader.getPageSize(1).getBottom();
			float top = reader.getPageSize(1).getTop();//792
			float height = reader.getPageSize(1).getHeight();//792
			float width = reader.getPageSize(1).getWidth();//612
			
			IntFunction<String> pdfToTextMapper = intUnchecked(
					pageIndex -> PdfTextExtractor
							.getTextFromPage(reader, pageIndex,
									new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter))
			);
			
			String data = IntStream.range(1, reader.getNumberOfPages())
					.mapToObj(pdfToTextMapper)
					.collect(Collectors.joining("\n"));
			
			String locationSegment = StringUtils
					.substringBetween(data, "Geelong, Rosemont", "STATUS\nPlease provide a");
			
			List<String> locationList = Arrays.stream(locationSegment.split("\n"))
					.filter(StringUtils::isNotEmpty)
					.collect(Collectors.toList());
			
			StringBuilder locationBuilder = new StringBuilder("Name\n");
			for (String location : locationList) {
				locationBuilder.append(location).append("\n");
			}
//			Files.write(Paths.get("src/main/resources/masterdata/location.csv"),
//					Collections.singleton(locationBuilder));
			
			//-------------------------------------------------------------
			
			String statusSegment = StringUtils
					.substringBetween(data, "eg., Full Training, Pre-Training, Spelling", "BARN AND BOX LIST");
			
			List<String> statusList = Arrays.stream(statusSegment.split("\n"))
					.filter(StringUtils::isNotEmpty)
					.collect(Collectors.toList());
			
			StringBuilder statusBuilder = new StringBuilder("Name\n");
			for (String status : statusList) {
				statusBuilder.append(status).append("\n");
			}
//			Files.write(Paths.get("src/main/resources/masterdata/status.csv"),
//					Collections.singleton(statusBuilder));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
