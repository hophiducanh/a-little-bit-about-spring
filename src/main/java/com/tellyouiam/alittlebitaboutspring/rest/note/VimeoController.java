package com.tellyouiam.alittlebitaboutspring.rest.note;

import com.clickntap.vimeo.Vimeo;
import com.clickntap.vimeo.VimeoException;
import com.clickntap.vimeo.VimeoResponse;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@RestController
@RequestMapping("/vimeo")
public class VimeoController { //https://developer.vimeo.com/api/reference/videos#edit_video
	
	public static void main(String[] args) throws IOException, VimeoException {
	
//		----------------------------PRISM ACCOUNT----------------------------- /videos/468841589
//     check skype for token
//		boolean upgradeTo1080 = true;
//		File file = new File("C:\\Users\\conta\\OneDrive\\Desktop\\Big_Buck_Bunny_720_10s_10MB.mp4");
		String videoEndPoint = "/videos/472472699";
//		String videoEndPoint = vimeo.addVideo(file, upgradeTo1080);
//		System.out.println(videoEndPoint);
//
//		VimeoResponse info = vimeo.getVideoInfo(videoEndPoint);
//		System.out.println(info);
		
//		String name = "Big Bug Bunny";
//		String desc = "The bunny lost in the darkness.";
//		String license = null; //see Vimeo API Documentation
//		String privacyView = "unlisted"; //see Vimeo API Documentation
//		String privacyEmbed = "whitelist"; //see Vimeo API Documentation
//		boolean reviewLink = false;
//		vimeo.updateVideoMetadata(videoEndPoint, name, desc, license, privacyView, privacyEmbed, reviewLink);
		//add video privacy domain
//		VimeoResponse response = vimeo.addVideoPrivacyDomain(videoEndPoint, "endota.fruitful.io");
//		System.out.println(response.getStatusCode());
//		vimeo.addVideoPrivacyDomain(videoEndPoint, "logbasex.wordpress.com");
//		vimeo.removeVideo(videoEndPoint);
//		String json = vimeo.get(videoEndPoint)
//				.getJson().toString();
//		System.out.println(vimeo.delete(videoEndPoint));

//		---------------------------MY ACCOUNT-----------------------------
		Vimeo vimeo = new Vimeo("9d49485793bfcfc65b36f978d1dbbdc7");
		URL url = new URL("https://prismhorse.s3.amazonaws.com/media/517cce7ebfa94b799874093c38c7a622.mp4");
		File file = new File("prism-horse.jpg");
		InputStream inputStream = url.openStream();
		FileUtils.copyURLToFile(url, file);
//		vimeo.addVideo(file, true);
		String name = "Big Bug Bunny";
		String desc = "The bunny lost in the darkness.";
		String license = null; //see Vimeo API Documentation
		String privacyView = "anybody"; //see Vimeo API Documentation
		String privacyEmbed = "public"; //see Vimeo API Documentation
		boolean reviewLink = false;
		VimeoResponse response = vimeo.updateVideoMetadata(videoEndPoint, name, desc, license, privacyView, privacyEmbed, reviewLink);
		System.out.println(response.getStatusCode());
	}
}
