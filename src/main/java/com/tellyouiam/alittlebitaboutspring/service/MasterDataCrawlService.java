package com.tellyouiam.alittlebitaboutspring.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface MasterDataCrawlService {
	Object crawMasterData(String masterDataLink) throws IOException;
}
