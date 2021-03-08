package com.tellyouiam.alittlebitaboutspring.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {
	
	@Value("${es.host}")
	private String host;
	
	@Value("${es.port}")
	private String port;
	
	@Override
	public RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
				.connectedTo(String.format("%s:%s", host, port))
				.build();
		
		return RestClients.create(clientConfiguration).rest();
	}
}
