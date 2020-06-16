package com.tellyouiam.alittlebitaboutspring.service.image;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static com.tellyouiam.alittlebitaboutspring.utils.stream.LambdaExceptionHelper.unchecked;

@Service
public class ImageService {
	
	private static void generateImageFromPDF(String filename, String extension) throws IOException {
		PDDocument document = PDDocument.load(new File(filename));
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
	//TODO current with bug: !strcmp(locale, "C"):Error:Assert failed:in file baseapi.cpp, line 209 (tesseract 4)
	//this bug already disappear in tesseract 5.
	public void manipulatePdf() throws IOException {
		Tesseract tesseract = new Tesseract();
		try {
			//not working perfectly
			
			//In case you don't have your own tessdata, let it also be extracted for you
			File tessDataFolder = LoadLibs.extractTessResources("tessdata");
			
			tesseract.setDatapath(tessDataFolder.getAbsolutePath());
			//tesseract.setDatapath("/home/logbasex/Downloads/Tess4J/tessdata");
			//tesseract.setLanguage("eng");
			//tesseract.setDatapath("./tesseract/libs/Tess4J/tessdata");
			
			File file = new File("src/main/resources/tesseract/images/pdf-image.png");
			//set DPI (Dot per inch) for an image: https://stackoverflow.com/questions/321736/how-to-set-dpi-information-in-an-image
			
			String text = tesseract.doOCR(file);
			System.out.print(text);

			String value = Files.walk(Paths.get("src/main/resources/tesseract/images"))
					.filter(Files::isRegularFile)
					.map(Path::toFile)
					.map(unchecked(tesseract::doOCR))
					.collect(Collectors.joining("\n"));

			System.out.println(value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//https://dzone.com/articles/pdfbox-java-library-to-extract-content-from-a-pdf
		//https://viblo.asia/p/doc-va-ghi-file-pdf-trong-java-l5XRBJQeRqPe
		
		String filePath = "src/main/resources/pdf-files/onboarding.pdf";
		try (PDDocument document = PDDocument.load(new File(filePath), MemoryUsageSetting.setupTempFileOnly())) {
			
			System.out.println(document.getClass());
			
			if (!document.isEncrypted()) {
				
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);
				
				PDFTextStripper tStripper = new PDFTextStripper();
				
				String pdfFileInText = tStripper.getText(document);
				System.out.println("Text:" + pdfFileInText);
			}
		}
		
		generateImageFromPDF(filePath, "png");
	}
	
	public static void main(String[] args) throws AWTException, IOException {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = new Robot().createScreenCapture(screenRect);
		//ImageIO.write(capture, "png", new File(" C:\\Users\\conta\\OneDrive\\Desktop\\screen.png"));
		
		//jsoup download image
		Connection.Response resultImageResponse = Jsoup.connect("https://www.prism.horse/assets/images/white-logo@2x.png")
				.ignoreContentType(true).execute();

		FileOutputStream out = (new FileOutputStream(new java.io.File("image url to save")));
		out.write(resultImageResponse.bodyAsBytes());  // resultImageResponse.body() is where the image's contents are.
		out.close();
	}
}
