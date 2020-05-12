package com.tellyouiam.alittlebitaboutspring.rest;

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

@RestController
@RequestMapping(value = "/image")
public class ImageController {
	
	@Autowired
	private ImageService imageSv;
	
	@PostMapping("/screen-shot")
	public final ResponseEntity<Object> automateImportHorse(@RequestPart(required = false) MultipartFile file) {
		Object result = imageSv.takeScreenShot(file);
		
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
}
