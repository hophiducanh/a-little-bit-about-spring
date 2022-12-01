package com.logbasex;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public interface LanguageProvider {
	
	List<String> getLanguages();
	
	@Component
	class DefaultLanguageProvider implements LanguageProvider {
		
		@Override
		public List<String> getLanguages() {
			List<String> languageList = new ArrayList<>();
			for (Locale locale : Locale.getAvailableLocales()) {
				languageList.add(locale.getDisplayLanguage());
			}
			return languageList;
		}
	}
}
