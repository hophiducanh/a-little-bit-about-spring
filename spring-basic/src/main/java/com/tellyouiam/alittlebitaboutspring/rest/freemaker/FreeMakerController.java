package com.tellyouiam.alittlebitaboutspring.rest.freemaker;

import com.tellyouiam.alittlebitaboutspring.service.freemaker.FreeMakerService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * https://github.com/hdineth/SpringBoot-freemaker-email-send/blob/master/src/main/resources/application.properties
 * */

@CrossOrigin(
		allowCredentials = "true",
		origins = "*",
		allowedHeaders = "*",
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT}
)
@RestController
@RequestMapping(value = "/pdf")
public class FreeMakerController {
	
	@Autowired
	private FreeMakerService freeMakerSv;
	
	@PostMapping
	public final ResponseEntity sendMail() throws MessagingException, IOException, TemplateException {
		
		freeMakerSv.sendMail();
		return new ResponseEntity<>("", HttpStatus.OK);
	}
}
