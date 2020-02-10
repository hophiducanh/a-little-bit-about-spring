package com.tellyouiam.alittlebitaboutspring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

/**
 * @author : Ho Anh
 * @since : 10/02/2020, Mon
 **/
@Configuration
@CacheConfig
public class CustomCacheConfig {
	private static final Logger logger = LoggerFactory
			.getLogger(CustomCacheConfig.class);
	
	@Bean
	public CacheManager cacheManager() {
		CachingProvider cachingProvider = Caching.getCachingProvider();
		CacheManager cacheManager = cachingProvider.getCacheManager();
		
		// The class arguments is <String, String> because the method to cache accepts a String and returns a String
		// just explore this object for the config you need.
		MutableConfiguration<String, String> configuration = new MutableConfiguration<>();
		
		String cacheName = "";
		cacheManager.createCache(cacheName, configuration);
		return cacheManager;
	}
}
