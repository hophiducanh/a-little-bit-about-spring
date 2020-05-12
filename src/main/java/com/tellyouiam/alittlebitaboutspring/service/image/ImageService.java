package com.tellyouiam.alittlebitaboutspring.service.image;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ImageService {
	
	public Object takeScreenShot(MultipartFile file) {
		return null;
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
