package com.logbasex.springbean.reinitialize.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service("ConfigManager")
public class ConfigManager {
	private static final Log LOG = LogFactory.getLog(ConfigManager.class);
	
	private Map<String,Object> config;
	
	private final String filePath;
	
	public ConfigManager(@Value("${config.file.path}") String filePath) {
		this.filePath = filePath;
		initConfigs();
	}
	
	// How to reload data from text file during runtime in Spring? https://stackoverflow.com/questions/56471862/how-to-reload-data-from-text-file-during-runtime-in-spring
	// IDE or build system might move your resources to your build directory and put that on the class path.
	// So the file you are editing in your source directory is not the file that is being served.
	// .getSourceAsStream() caches internally.
	private void initConfigs() {
		Properties properties = new Properties();
		URL resource = getClass().getClassLoader().getResource(filePath);
		if (resource == null) return;
		
		try (InputStream inputStream = resource.openStream()){
			properties.load(inputStream);
		} catch (IOException e) {
			LOG.error("Error loading configuration:", e);
		}
		config = new HashMap<>();
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			config.put(String.valueOf(entry.getKey()), entry.getValue());
		}
	}
	
	public Object getConfig(String key) {
		return config.get(key);
	}
	
	public void reinitializeConfig() {
		initConfigs();
	}
}
