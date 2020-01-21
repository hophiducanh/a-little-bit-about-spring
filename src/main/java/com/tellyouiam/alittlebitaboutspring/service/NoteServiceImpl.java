package com.tellyouiam.alittlebitaboutspring.service;

import com.tellyouiam.alittlebitaboutspring.utils.OnboardHelper;
import com.tellyouiam.alittlebitaboutspring.utils.StringHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService{
	
	private int check(String[] arr, String valueToCheck) {
		int index;
		for (String element : arr) {
			if (element.equalsIgnoreCase(valueToCheck)) {
				index = Arrays.asList(arr).indexOf(valueToCheck);
				if (index != -1) {
					return index;
				}
			}
		}
		return 0;
	}
	
	private List<String> getCsvData(MultipartFile multipart) throws IOException {
		InputStream is = multipart.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		return this.getCsvData(br, false);
	}
	
	public  List<String> getCsvData(BufferedReader bufReader) throws IOException {
		return getCsvData(bufReader, true);
	}
	
	private List<String> getCsvData(BufferedReader bufReader, boolean ignoreHeader) throws IOException {
		List<String> data = new ArrayList<>();
		String line = null;
		int count = 0;
		while ((line = bufReader.readLine()) != null) {
			count++;
			if (ignoreHeader && count == 1)
				continue;
			data.add(line);
		}
		return data;
	}
	
	
	
	@Override
	public Object automateImportOwner(MultipartFile ownerFile) {
		try {
			List<String> csvData = this.getCsvData(ownerFile);
			StringBuilder builder = new StringBuilder();
			if (!CollectionUtils.isEmpty(csvData)) {
				
				// ---------- common cols --------------------------------------
				// OWNER_KEY (ID or EMAIL), can leave blank if ID is EMAIL
				// EMAIL
				// FINANCE EMAIL
				// FIRST NAME
				// LAST NAME
				// DISPLAY NAME
				// TYPE
				// MOBILE
				// PHONE
				// FAX
				// ADDRESS
				// SUBURB (CITY)
				// STATE
				// POSTCODE
				// COUNRTY
				// GST = "true/false" or ot "T/F" or "Y/N"
				// -------------------------------------------------------------
				
				String[] header = OnboardHelper.readCsvLine(csvData.get(0));
				
				int ownerIdIndex = check(header, "OwnerID");
				int emailIndex = check(header, "Email");
				int financeEmailIndex = check(header, "FinanceEmail");
				int firstNameIndex = check(header, "FirstName");
				int lastNameIndex = check(header, "LastName");
				int displayNameIndex = check(header, "DisplayName");
				int typeIndex = check(header, "Type");
				int mobileIndex = check(header, "Mobile");
				int phoneIndex = check(header, "Phone");
				int faxIndex = check(header, "Fax");
				int addressIndex = check(header, "Address");
				int cityIndex = check(header, "City");
				int stateIndex = check(header, "State");
				int postCodeIndex = check(header, "PostCode");
				int countryIndex = check(header, "Country");
				int gstIndex = check(header, "GST");
				
				String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
						"OwnerID", "Email", "FinanceEmail", "FirstName", "LastName", "DisplayName",
						"Type", "Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode",
						"Country", "GST"
						);
				
				builder.append(rowHeader);
				
				csvData = csvData.stream().skip(1).collect(Collectors.toList());
				for (String line : csvData) {
					String[] r = OnboardHelper.readCsvLine(line);
					
					String ownerId = OnboardHelper.readCsvRow(r, ownerIdIndex);
					String email = OnboardHelper.readCsvRow(r, emailIndex);
					String financeEmail = OnboardHelper.readCsvRow(r, financeEmailIndex);
					String firstName = OnboardHelper.readCsvRow(r, firstNameIndex);
					String lastName = OnboardHelper.readCsvRow(r, lastNameIndex);
					String displayName = OnboardHelper.readCsvRow(r, displayNameIndex);
					String type = OnboardHelper.readCsvRow(r, typeIndex);
					String mobile = OnboardHelper.readCsvRow(r, mobileIndex);
					String phone = OnboardHelper.readCsvRow(r, phoneIndex);
					String fax = OnboardHelper.readCsvRow(r, faxIndex);
					String address = OnboardHelper.readCsvRow(r, addressIndex);
					String city = OnboardHelper.readCsvRow(r, cityIndex);
					String state = OnboardHelper.readCsvRow(r, stateIndex);
					String postCode = OnboardHelper.readCsvRow(r, postCodeIndex);
					String country = OnboardHelper.readCsvRow(r, countryIndex);
					String gst = OnboardHelper.readCsvRow(r, gstIndex);
					
					String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
							StringHelper.csvValue(ownerId),
							StringHelper.csvValue(email),
							StringHelper.csvValue(financeEmail),
							StringHelper.csvValue(firstName),
							StringHelper.csvValue(lastName),
							StringHelper.csvValue(displayName),
							StringHelper.csvValue(type),
							StringHelper.csvValue(mobile),
							StringHelper.csvValue(phone),
							StringHelper.csvValue(fax),
							StringHelper.csvValue(address),
							StringHelper.csvValue(city),
							StringHelper.csvValue(state),
							StringHelper.csvValue(postCode),
							StringHelper.csvValue(country),
							StringHelper.csvValue(gst)
					);
					builder.append(rowBuilder);
				}
			}
			return builder;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
