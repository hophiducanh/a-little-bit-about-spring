package com.tellyouiam.alittlebitaboutspring.rest.image;

import com.tellyouiam.alittlebitaboutspring.service.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/image")
public class ImageController {
	
	@Autowired
	private ImageService imageSv;
	
	@PostMapping("/pdf")
	public final ResponseEntity<Object> manipulatePdf() {
		try {
			imageSv.manipulatePdf();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>( HttpStatus.OK);
	}
}
