package com.tellyouiam.alittlebitaboutspring.service.masterdata;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

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
	
	//https://www.geeksforgeeks.org/tesseract-ocr-with-java-with-examples/
	//https://medium.com/@rahulvaish/simple-tesseract-ocr-java-be261e343c5b
	//https://github.com/eugenp/tutorials/tree/master/image-processing/src/main
	//https://stackoverflow.com/questions/55036633/how-to-create-traineddata-file-for-tesseract-4-1-0
	public static void main(String[] args) throws IOException, TesseractException {
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
			//String text1 = tesseract.doOCR(new File("tesseract/images/web-image.png"), new Rectangle(0,200,1448, 200));
			System.out.print(text);
			//System.out.print(text1);
		} catch (TesseractException e) {
			e.printStackTrace();
		}
	}
}
