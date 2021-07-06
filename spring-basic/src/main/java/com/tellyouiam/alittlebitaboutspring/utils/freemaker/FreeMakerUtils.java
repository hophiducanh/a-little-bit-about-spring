package com.tellyouiam.alittlebitaboutspring.utils.freemaker;

import com.itextpdf.tool.xml.XMLWorkerHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.apache.commons.codec.CharEncoding.UTF_8;

public class FreeMakerUtils {
	public static String loadFtlHtml(File baseDir, String fileName, Map globalMap) {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
		try {
			cfg.setDirectoryForTemplateLoading(baseDir);
			cfg.setDefaultEncoding(UTF_8);
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			cfg.setClassicCompatible(true);
			Template template = cfg.getTemplate(fileName);
			
			StringWriter writer = new StringWriter();
			template.process(globalMap, writer);
			return writer.toString();
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
		}
		return null;
	}
}
