package com.tellyouiam.alittlebitaboutspring.rest.dummy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dummy")
public class DummyController {
	
	//test if spring is multi-threaded
	//https://stackoverflow.com/questions/46223363/spring-boot-handle-multiple-requests-concurrently
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> dummy(@RequestParam(value="msg", defaultValue="Hello") String msg) {
		System.out.println("" + new Date() + ": ThreadId " + Thread.currentThread().getId());
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, String> response = new HashMap<>();
		response.put("message", msg);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}