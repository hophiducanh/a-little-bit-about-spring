package com.tellyouiam.alittlebitaboutspring.service.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchService {
	
	@Autowired
	private RestHighLevelClient client;
	
	public void search() {
		MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("Duke")
				.field("th_name", 2.0f)
				.field("th_description")
				.field("th_show_title")
				.field("content");
		
//		client.msearch(queryBuilder)
	}
}
