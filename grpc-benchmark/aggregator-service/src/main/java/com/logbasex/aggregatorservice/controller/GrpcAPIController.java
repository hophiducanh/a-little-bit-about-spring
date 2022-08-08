package com.logbasex.aggregatorservice.controller;

import com.logbasex.aggregatorservice.service.GrpcAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("grpc")
public class GrpcAPIController {
	
	@Autowired
	private GrpcAPIService grpcAPISv;
	
	@GetMapping("/unary/{number}")
	public Object getResponseUnary(@PathVariable int number){
		return this.grpcAPISv.getSquareResponseUnary(number);
	}
	
//	@GetMapping("/stream/{number}")
//	public Object getResponseStream(@PathVariable int number){
//		return this.service.getSquareResponseStream(number);
//	}
}
