package com.tellyouiam.alittlebitaboutspring.service.masterdata;

import com.tellyouiam.alittlebitaboutspring.utils.LambdaExceptionHelper;
import lombok.SneakyThrows;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.ghost4j.document.PDFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
	
	private static void generateImageFromPDF(String filename, String extension) throws IOException {
		PDDocument  document = PDDocument.load(new File(filename));
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		for (int page = 0; page < document.getNumberOfPages(); ++page) {
			BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
			ImageIOUtil.writeImage(bim, String.format("pdf-%d.%s", page + 1, extension), 300);
		}
	}
	
	//https://www.geeksforgeeks.org/tesseract-ocr-with-java-with-examples/
	//https://medium.com/@rahulvaish/simple-tesseract-ocr-java-be261e343c5b
	//https://github.com/eugenp/tutorials/tree/master/image-processing/src/main
	//https://stackoverflow.com/questions/55036633/how-to-create-traineddata-file-for-tesseract-4-1-0
	@SneakyThrows
	public static void main(String[] args) throws IOException {
		Tesseract tesseract = new Tesseract();
		try {
			//not working perfectly
			
			//In case you don't have your own tessdata, let it also be extracted for you
			File tessDataFolder = LoadLibs.extractTessResources("tessdata");
			
			tesseract.setDatapath(tessDataFolder.getAbsolutePath());
			tesseract.setLanguage("eng");
			//tesseract.setDatapath("./tesseract/libs/Tess4J/tessdata");
			
			File file = new File("src\\main\\resources\\tesseract\\images\\pdf-image.png");
			String text = tesseract.doOCR(file);
			//String text1 = tesseract.doOCR(new File("C:\\Users\\conta\\OneDrive\\Desktop\\Trainer Onboarding _ Prism.pdf"), new Rectangle(0,200,1448, 200));
			System.out.print(text);
			//System.out.print(text1);
			
			String value = Files.walk(Paths.get("src\\main\\resources\\tesseract\\images"))
					.filter(Files::isRegularFile)
					.map(Path::toFile)
					.map(LambdaExceptionHelper.unchecked(tesseract::doOCR))
					.collect(Collectors.joining("\n"));
			
			System.out.println(value);
			
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		
		//https://dzone.com/articles/pdfbox-java-library-to-extract-content-from-a-pdf
		//https://viblo.asia/p/doc-va-ghi-file-pdf-trong-java-l5XRBJQeRqPe
		
		String filePath = "src\\main\\resources\\pdf-files\\onboarding.pdf";
		try (PDDocument document = PDDocument.load(new File(filePath), MemoryUsageSetting.setupTempFileOnly())) {
			
			System.out.println(document.getClass());
			
			if (!document.isEncrypted()) {
				
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);
				
				PDFTextStripper tStripper = new PDFTextStripper();
				
				String pdfFileInText = tStripper.getText(document);
				System.out.println("Text:" + pdfFileInText);
				
				// split by whitespace
//				String[] lines = pdfFileInText.split("\\r?\\n");
//				for (String line : lines) {
//					System.out.println(line);
//				}
				
			}
		}
		
		//generateImageFromPDF(filePath, "png");
	}
}
