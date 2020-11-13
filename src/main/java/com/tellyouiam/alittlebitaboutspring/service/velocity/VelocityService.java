package com.tellyouiam.alittlebitaboutspring.service.velocity;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VelocityService {
	
	private static final Logger logger = LoggerFactory.getLogger(VelocityService.class);
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	public String createContentWithTemplate(String pathToTemplateFile, VelocityContext context) {
		String bodyContent = null;
		
		try {
			if (StringUtils.isNotEmpty(pathToTemplateFile)) {
				//$date.format('EEE, MMM d, yyyy at ha', $myDate)
				//https://stackoverflow.com/questions/15092372/how-to-select-the-format-of-date-in-vm-file
				context.put("date", new DateTool());
				
				context.put("number", new NumberTool());
				
				context.put("logger", logger);
				
				velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
//				velocityEngine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
				velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
				velocityEngine.init();
				Template template = velocityEngine.getTemplate( pathToTemplateFile);
				template.setEncoding(CharEncoding.UTF_8);
				StringWriter writer = new StringWriter();
				template.merge( context, writer);
				
//				velocityEngine.mergeTemplate(pathToTemplateFile, "UTF-8", context, stringWriter);
				bodyContent = writer.toString();
			}
		} catch (ResourceNotFoundException | ParseErrorException | MethodInvocationException e) {
			e.printStackTrace();
		}
		return bodyContent;
	}
	
	public String testVelocitySyntax() {
		List<FinancePaymentTransactionResponse> txs = new ArrayList<>(); // get first value in case trainer
		txs.add(new FinancePaymentTransactionResponse(10D, 5D));
		txs.add(new FinancePaymentTransactionResponse(5D, 9D));
		txs.add(new FinancePaymentTransactionResponse(8D, 100D));
		HashMap<String, Object> map = new HashMap<>();
		map.put("paymentTransactions", txs);
		VelocityContext context = new VelocityContext(map);
		return createContentWithTemplate("templates/velocity-example.html", context);
	}
	
	private class FinancePaymentTransactionResponse {
		private Double formattedCashRefundAmount = 0D;
		private Double formattedAmountOriginal = 0D;
		
		public FinancePaymentTransactionResponse(Double formattedCashRefundAmount, Double formattedAmountOriginal) {
			this.formattedCashRefundAmount = formattedCashRefundAmount;
			this.formattedAmountOriginal = formattedAmountOriginal;
		}
		
		public Double getFormattedCashRefundAmount() {
			return formattedCashRefundAmount;
		}
		
		public void setFormattedCashRefundAmount(Double formattedCashRefundAmount) {
			this.formattedCashRefundAmount = formattedCashRefundAmount;
		}
		
		public Double getFormattedAmountOriginal() {
			return formattedAmountOriginal;
		}
		
		public void setFormattedAmountOriginal(Double formattedAmountOriginal) {
			this.formattedAmountOriginal = formattedAmountOriginal;
		}
	}
}