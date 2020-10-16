package com.tellyouiam.alittlebitaboutspring.rest.note;

import com.clickntap.vimeo.Vimeo;
import com.clickntap.vimeo.VimeoException;
import com.clickntap.vimeo.VimeoResponse;
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

@RestController
@RequestMapping("/vimeo")
public class VimeoController {
	
	public static void main(String[] args) throws IOException, VimeoException {
		Vimeo vimeo = new Vimeo("c38598d800727386e68aa930f4f4735e");
		boolean upgradeTo1080 = true;
		File file = new File("C:\\Users\\conta\\OneDrive\\Desktop\\Big_Buck_Bunny_720_10s_10MB.mp4");
		String videoEndPoint = "/videos/468841589";
//		String videoEndPoint = vimeo.addVideo(file, upgradeTo1080);
//		System.out.println(videoEndPoint);
//
//		VimeoResponse info = vimeo.getVideoInfo(videoEndPoint);
//		System.out.println(info);
		
		String name = "Big Bug Bunny";
		String desc = "The bunny lost in the darkness.";
		String license = null; //see Vimeo API Documentation
		String privacyView = "anybody"; //see Vimeo API Documentation
		String privacyEmbed = "unlisted"; //see Vimeo API Documentation
		boolean reviewLink = false;
		VimeoResponse response = vimeo.updateVideoMetadata(videoEndPoint, name, desc, license, privacyView, privacyEmbed, reviewLink);
		//add video privacy domain
		vimeo.addVideoPrivacyDomain(videoEndPoint, "endota.fruitful.io");
//		vimeo.removeVideo(videoEndPoint);
		System.out.println(response.getJson());
	}
}
