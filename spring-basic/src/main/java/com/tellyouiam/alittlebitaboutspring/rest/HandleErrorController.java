package com.tellyouiam.alittlebitaboutspring.rest;

import org.apache.http.HttpStatus;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HandleErrorController implements ErrorController {
	
	@RequestMapping("/error")
	public String handleError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		
		if (status != null) {
			int statusCode = Integer.parseInt(status.toString());
			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				return "error-404";
			} else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
				return "error-500";
			}
		}
		return "error";
	}
	
	@Override
	public String getErrorPath() {
		return "/error";
	}
}
