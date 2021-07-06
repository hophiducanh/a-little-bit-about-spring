package com.tellyouiam.alittlebitaboutspring.rest.cors;

import com.tellyouiam.alittlebitaboutspring.dto.cors.Account;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/account")
public class AccountController {
	
//	@CrossOrigin("http://example.com")
	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	public Account retrieve(@PathVariable Long id) {
		return new Account(id, null);
	}
}
