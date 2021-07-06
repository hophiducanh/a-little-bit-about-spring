package com.tellyouiam.alittlebitaboutspring.service.freemaker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class FreeMakerService {
	private static final Logger logger = LoggerFactory.getLogger(FreeMakerService.class);
	
	@Autowired
	private JavaMailSender emailSender;
	
	@Autowired
	@Qualifier("emailConfigBean")
	private Configuration emailConfig;
	
	public void sendMail() throws MessagingException, IOException, TemplateException {
		Map<Object, Object> model = new HashMap<>();
		model.put("name", "Logbasex");
		model.put("location", "Sri Lanka");
		model.put("signature", "https://techmagister.info");
		model.put("content", "Welcome Lady");
		
		logger.info("Sending email to {}", "dat.pham@fruitful.io");
		
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
		mimeMessageHelper.addInline("logo.png", new ClassPathResource("classpath:/techmagisterLogo.png"));
		
		Template template = emailConfig.getTemplate("email.ftl");
		String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		
		mimeMessageHelper.setTo("dat.pham@fruitful.io");
		mimeMessageHelper.setText(html, true);
		mimeMessageHelper.setSubject("Welcome Lady.");
		mimeMessageHelper.setFrom("anh.ho@fruitful.io");
		
		emailSender.send(message);
	}
}
