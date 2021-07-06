package com.tellyouiam.alittlebitaboutspring.rest.masterdata;

import com.tellyouiam.alittlebitaboutspring.service.masterdata.MasterDataCrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/master-data")
public class MasterDataCrawToolController {
	
	@Autowired
	private MasterDataCrawlService masterDataCrawlService;
	
	@RequestMapping(value = "/crawl", method = RequestMethod.POST)
	@ResponseBody
	public final ResponseEntity<Object> prepareOwnership(@RequestParam(required = false) String masterDataLink) {
		Object result = null;
		try {
			result = masterDataCrawlService.crawMasterData(masterDataLink);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.OK);
	}
}
