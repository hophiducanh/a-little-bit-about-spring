package com.tellyouiam.alittlebitaboutspring.service;

import com.tellyouiam.alittlebitaboutspring.exception.CustomException;
import com.tellyouiam.alittlebitaboutspring.utils.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.tellyouiam.alittlebitaboutspring.utils.OnboardHelper.*;
import static java.time.temporal.ChronoField.*;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

    private static final String HORSE_FILE_HEADER = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
            "OwnerID", "Email", "FinanceEmail", "FirstName", "LastName", "DisplayName", "Type",
            "Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode", "Country", "GST");

    private int checkColumnIndex(String[] arr, String... valuesToCheck) {
        int index;
        for (String element : arr) {
            for (String value : valuesToCheck) {
                String formattedElement = element.replace("\"", "").trim();
                if (formattedElement.equalsIgnoreCase(value)) {
                    index = Arrays.asList(arr).indexOf(element);
                    return index;
                }
            }
        }
        return -1;
    }

    private List<String> getCsvData(MultipartFile multipart) throws IOException {
        InputStream is = multipart.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        return this.getCsvData(br, false);
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

    private static boolean isRecognizedAsValidDate(String dateStr) {
        Matcher dateMatcher = Pattern.compile(IS_INSTANCEOF_DATE_PATTERN).matcher(dateStr);
        return dateMatcher.matches();
    }

    private static String[][] readCSVTo2DArray(String path, boolean ignoreHeader) throws FileNotFoundException, IOException {
        try (FileReader fr = new FileReader(path);
             BufferedReader br = new BufferedReader(fr)) {
            Collection<String[]> lines = new ArrayList<>();
            int count = 0;
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.length() > 0) {
                    count++;
                    if (ignoreHeader && count == 1) {
                        continue;
                    }

                    lines.add(readCsvLine(line));
                }
            }
            return lines.toArray(new String[lines.size()][]);
        }
    }

    private String getOutputFolder(String dirName) {
        String initFolderPath = getOutputFolderPath();
        Path outputDirPath = Paths.get(Objects.requireNonNull(initFolderPath), dirName, "submit");

        Path path = null;
        boolean dirExists = Files.exists(outputDirPath);
        if (!dirExists) {
            try {
                path = Files.createDirectories(outputDirPath);
            } catch (IOException io) {
                logger.error("Error occur when create the folder at: {}", outputDirPath.toAbsolutePath().toString());
            }
        }
        return dirExists ? outputDirPath.toAbsolutePath().toString() : Objects.requireNonNull(path).toString();
    }

    private static final String HORSE_RECORDS_PATTERN = "([\\d]+)\\sRecords"; //like: 162 records

    @Override
    public Object automateImportOwner(MultipartFile ownerFile, String dirName) throws CustomException {
        try {
            List<String> csvData = this.getCsvData(ownerFile);
            List<String> preparedData = new ArrayList<>();
            StringBuilder builder = new StringBuilder();

            String ownerErrorData = StringUtils.EMPTY;

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

                String[] header = readCsvLine(csvData.get(0));

                int ownerIdIndex = checkColumnIndex(header, "OwnerID");
                int emailIndex = checkColumnIndex(header, "Email");
                int financeEmailIndex = checkColumnIndex(header, "FinanceEmail");
                int firstNameIndex = checkColumnIndex(header, "FirstName", "First Name");
                int lastNameIndex = checkColumnIndex(header, "LastName", "Last Name");
                int displayNameIndex = checkColumnIndex(header, "DisplayName", "Name", "Display Name");
                int typeIndex = checkColumnIndex(header, "Type");
                int mobileIndex = checkColumnIndex(header, "Mobile", "Mobile Phone");
                int phoneIndex = checkColumnIndex(header, "Phone");
                int faxIndex = checkColumnIndex(header, "Fax");
                int addressIndex = checkColumnIndex(header, "Address");
                int cityIndex = checkColumnIndex(header, "City");
                int stateIndex = checkColumnIndex(header, "State");
                int postCodeIndex = checkColumnIndex(header, "PostCode");
                int countryIndex = checkColumnIndex(header, "Country");
                int gstIndex = checkColumnIndex(header, "GST");

                builder.append(HORSE_FILE_HEADER);

                csvData = csvData.stream().skip(1).collect(Collectors.toList());

                for (String line : csvData) {
                    if (StringUtils.isEmpty(line)) continue;

                    String[] r = readCsvLine(line);

                    //rows will be ignored like:
                    //,,,,
                    //162 Records,,,,

                    StringBuilder ignoreRowBuilder = new StringBuilder();
                    for (String s : r) {
                        ignoreRowBuilder.append(s);
                    }
                    if (StringUtils.isEmpty(ignoreRowBuilder.toString())) continue;

                    if (StringUtils.isEmpty(ignoreRowBuilder.toString().replaceAll(HORSE_RECORDS_PATTERN, ""))) {
                        logger.info("\n*******************Ignored Horse Records Line: {}", ignoreRowBuilder.toString());
                        continue;
                    }

                    String ownerId = getCsvCellValue(r, ownerIdIndex);
                    String email = getCsvCellValue(r, emailIndex);
                    String financeEmail = getCsvCellValue(r, financeEmailIndex);
                    String firstName = getCsvCellValue(r, firstNameIndex);
                    String lastName = getCsvCellValue(r, lastNameIndex);
                    String displayName = getCsvCellValue(r, displayNameIndex);
                    String type = getCsvCellValue(r, typeIndex);

                    String mobile = getCsvCellValue(r, mobileIndex);

                    String phone = getCsvCellValue(r, phoneIndex);

                    String fax = getCsvCellValue(r, faxIndex);
                    String address = getCsvCellValue(r, addressIndex);
                    String city = getCsvCellValue(r, cityIndex);
                    String state = getCsvCellValue(r, stateIndex);
                    String postCode = getPostcode(getCsvCellValue(r, postCodeIndex));
                    String country = getCsvCellValue(r, countryIndex);
                    String gst = getCsvCellValue(r, gstIndex);

                    String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
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
                    preparedData.add(rowBuilder);

                    builder.append(rowBuilder);
                }

                ownerErrorData = CsvHelper.validateInputFile(preparedData);
            }

            String errorDataPath = getOutputFolder(dirName) + File.separator + "owner-input-error.csv";
            FileHelper.writeDataToFile(errorDataPath, ownerErrorData.getBytes());

            String path = getOutputFolder(dirName) + File.separator + "formatted-owner.csv";
            FileHelper.writeDataToFile(path, builder.toString().getBytes());

            return builder;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object importHorseFromMiStable(MultipartFile horseFile, String dirName) {

        try {
            String path = getOutputFolder(dirName).concat(File.separator).concat("formatted-horse.csv");

            List<String> csvData = this.getCsvData(horseFile);
            csvData = csvData.stream().filter(org.apache.commons.lang3.StringUtils::isNotEmpty).collect(Collectors.toList());
            StringBuilder builder = new StringBuilder();

            StringBuilder addedDateBuilder = new StringBuilder();
            StringBuilder activeStatusBuilder = new StringBuilder();
            StringBuilder currentLocationBuilder = new StringBuilder();

            if (!CollectionUtils.isEmpty(csvData)) {

                // read from horse file first, standard columns order:
                // EXTERNAL ID, can leave blank if use hash code from name as id
                // NAME
                // FOALED
                // SIRE
                // DAM
                // COLOUR
                // SEX
                // AVATAR
                // ADDED DATE
                // STATUS (active/inactive)
                // CURRENT LOCATION
                // CURRENT STATUS
                // TYPE (Race Horse/ Stallion/ Speller/ Brood Mare/ Yearling)
                // CATEGORY
                // BONUS SCHEME
                // NICK NAME

                String[] header = readCsvLine(csvData.get(0));

                int externalIdIndex = checkColumnIndex(header, "ExternalId");
                int nameIndex = checkColumnIndex(header, "Horse Name", "Name", "Horse");
                int foaledIndex = checkColumnIndex(header, "DOB", "foaled");
                int sireIndex = checkColumnIndex(header, "Sire");
                int damIndex = checkColumnIndex(header, "Dam");
                int colorIndex = checkColumnIndex(header, "Color");
                int sexIndex = checkColumnIndex(header, "Gender", "Sex");
                int avatarIndex = checkColumnIndex(header, "Avatar");
                int addedDateIndex = checkColumnIndex(header, "AddedDate");
                int activeStatusIndex = checkColumnIndex(header, "Active Status", "ActiveStatus");
                int horseLocationIndex = checkColumnIndex(header, "Property");
                int horseStatusIndex = checkColumnIndex(header, "Current Status", "CurrentStatus");
                int typeIndex = checkColumnIndex(header, "Type");
                int categoryIndex = checkColumnIndex(header, "Category");
                int bonusSchemeIndex = checkColumnIndex(header, "Bonus Scheme", "BonusScheme", "Schemes");
                int nickNameIndex = checkColumnIndex(header, "Nick Name", "NickName");

                String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        "ExternalId", "Name", "Foaled", "Sire", "Dam", "Color",
                        "Sex", "Avatar", "AddedDate", "ActiveStatus/Status",
                        "CurrentLocation/HorseLocation", "CurrentStatus/HorseStatus",
                        "Type", "Category", "BonusScheme", "NickName"
                );

                builder.append(rowHeader);

                csvData = csvData.stream().skip(1).collect(Collectors.toList());
                for (String line : csvData) {
                    String[] r = readCsvLine(line);

                    String externalId = getCsvCellValue(r, externalIdIndex);
                    String name = getCsvCellValue(r, nameIndex);

                    String rawFoaled = getCsvCellValue(r, foaledIndex);
                    String foaled = StringUtils.EMPTY;

                    boolean isAustraliaFormat = isAustraliaFormat(csvData, foaledIndex, "horse");

                    if (!isAustraliaFormat && StringUtils.isNotEmpty(rawFoaled)) {
                        foaled = LocalDate.parse(rawFoaled, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
                    } else {
                        foaled = rawFoaled;
                    }

                    String sire = getCsvCellValue(r, sireIndex);
                    String dam = getCsvCellValue(r, damIndex);
                    String color = getCsvCellValue(r, colorIndex);
                    String sex = getCsvCellValue(r, sexIndex);

                    String avatar = getCsvCellValue(r, avatarIndex);

                    String addedDate = getCsvCellValue(r, addedDateIndex);
                    addedDateBuilder.append(addedDate);

                    String activeStatus = getCsvCellValue(r, activeStatusIndex);
                    addedDateBuilder.append(activeStatus);

                    String currentLocation = getCsvCellValue(r, horseLocationIndex);
                    addedDateBuilder.append(currentLocation);

                    String currentStatus = getCsvCellValue(r, horseStatusIndex);
                    String type = getCsvCellValue(r, typeIndex);
                    String category = getCsvCellValue(r, categoryIndex);
                    String bonusScheme = getCsvCellValue(r, bonusSchemeIndex);
                    String nickName = getCsvCellValue(r, nickNameIndex);

                    String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                            StringHelper.csvValue(externalId),
                            StringHelper.csvValue(name),
                            StringHelper.csvValue(foaled),
                            StringHelper.csvValue(sire),
                            StringHelper.csvValue(dam),
                            StringHelper.csvValue(color),
                            StringHelper.csvValue(sex),
                            StringHelper.csvValue(avatar),
                            StringHelper.csvValue(addedDate),
                            StringHelper.csvValue(activeStatus),
                            StringHelper.csvValue(currentLocation),
                            StringHelper.csvValue(currentStatus),
                            StringHelper.csvValue(type),
                            StringHelper.csvValue(category),
                            StringHelper.csvValue(bonusScheme),
                            StringHelper.csvValue(nickName)
                    );
                    builder.append(rowBuilder);
                }

                // Address case addedDate, activeStatus and current location in horse file are empty.
                // We will face with an error if we keep this data intact.
                if (StringUtils.isAllEmpty(addedDateBuilder, activeStatusBuilder, currentLocationBuilder)) {
                    logger.warn("All of AddedDate && ActiveStatus && CurrentLocation can't be empty. At least addedDate required.");

                    List<String> formattedData = StringHelper.convertStringBuilderToList(builder);
                    StringBuilder dataBuilder = new StringBuilder();

                    String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    if (!CollectionUtils.isEmpty(formattedData)) {
                        String[] formattedHeader = readCsvLine(formattedData.get(0));

                        //Get addedDate index from header
                        int addedDateOrdinal = checkColumnIndex(formattedHeader, "AddedDate");

                        //Append a header at first line of StringBuilder data to write to file.
                        dataBuilder.append(formattedData.get(0)).append("\n");

                        //process data ignore header
                        for (String line : formattedData.stream().skip(1).collect(Collectors.toList())) {

                            String[] row = readCsvLine(line);

                            for (int i = 0; i < row.length; i++) {

                                //replace empty addedDate with current date.
                                if (i == addedDateOrdinal) {
                                    row[addedDateOrdinal] = currentDate;
                                    dataBuilder.append(row[i]).append(",");
                                    continue;
                                }
                                dataBuilder.append(row[i]).append(",");
                            }
                            dataBuilder.append("\n");
                        }
                    }

                    if (dataBuilder.toString().contains(currentDate)) {
                        logger.info("******************** Successfully generated addedDate with dd/MM/yyyy format : {}", currentDate);
                    } else {
                        logger.error("******************** Error created when trying to attach generated addedDate to output file.");
                    }

                    try {
                        File file = new File(path);

                        FileOutputStream os = new FileOutputStream(file);
                        os.write(dataBuilder.toString().getBytes());
                        os.flush();
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return dataBuilder;
                }
            }

            try {
                File file = new File(path);

                FileOutputStream os = new FileOutputStream(file);
                os.write(builder.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return builder;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Object automateImportHorse(MultipartFile horseFile, MultipartFile ownershipFile, String dirName) throws CustomException {
        if (Objects.isNull(ownershipFile)) {
            return this.importHorseFromMiStable(horseFile, dirName);
        }

        Map<Object, Object> ownerShipResult = this.automateImportOwnerShip(ownershipFile);
        Map<Object, Object> result = new HashMap<>();

        String csvExportedDateStr = String.valueOf(ownerShipResult.get("ExportedDate"));

        try {
            List<String> csvData = this.getCsvData(horseFile);
            csvData = csvData.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());

            StringBuilder builder = new StringBuilder();
            if (!CollectionUtils.isEmpty(csvData)) {

                // read from horse file first, standard columns order:
                // EXTERNAL ID, can leave blank if use hash code from name as id
                // NAME
                // FOALED
                // SIRE
                // DAM
                // COLOUR
                // SEX
                // AVATAR
                // ADDED DATE
                // STATUS (active/inactive)
                // CURRENT LOCATION
                // CURRENT STATUS
                // TYPE (Race Horse/ Stallion/ Speller/ Brood Mare/ Yearling)
                // CATEGORY
                // BONUS SCHEME
                // NICK NAME

                String[] header = readCsvLine(csvData.get(0));

                int externalIdIndex = checkColumnIndex(header, "ExternalId");
                int nameIndex = checkColumnIndex(header, "Horse Name", "Name", "Horse");
                int foaledIndex = checkColumnIndex(header, "DOB", "foaled");
                int sireIndex = checkColumnIndex(header, "Sire");
                int damIndex = checkColumnIndex(header, "Dam");
                int colorIndex = checkColumnIndex(header, "Color");
                int sexIndex = checkColumnIndex(header, "Gender", "Sex");
                int avatarIndex = checkColumnIndex(header, "Avatar");
                int addedDateIndex = checkColumnIndex(header, "AddedDate");
                int activeStatusIndex = checkColumnIndex(header, "Active Status", "ActiveStatus");
                int horseLocationIndex = checkColumnIndex(header, "Property");
                int horseStatusIndex = checkColumnIndex(header, "Current Status", "CurrentStatus");
                int typeIndex = checkColumnIndex(header, "Type");
                int categoryIndex = checkColumnIndex(header, "Category");
                int bonusSchemeIndex = checkColumnIndex(header, "Bonus Scheme", "BonusScheme", "Schemes");
                int nickNameIndex = checkColumnIndex(header, "Nick Name", "NickName");

                int daysHereIndex = checkColumnIndex(header, "Days Here", "Days");

                String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        "ExternalId", "Name", "Foaled", "Sire", "Dam", "Color",
                        "Sex", "Avatar", "AddedDate", "ActiveStatus/Status",
                        "CurrentLocation/HorseLocation", "CurrentStatus/HorseStatus",
                        "Type", "Category", "BonusScheme", "NickName"
                );

                builder.append(rowHeader);

                csvData = csvData.stream().skip(1).collect(Collectors.toList());

                Map<String, String> horseMap = new LinkedHashMap<>();
                Map<String, String> horseOwnershipMap = (Map<String, String>) ownerShipResult.get("HorseDataMap");

                boolean isAustraliaFormat = isAustraliaFormat(csvData, foaledIndex, "horse");

                int count = 1;
                for (String line : csvData) {

                    count++;

                    if (StringUtils.isEmpty(line)) continue;

                    if (line.matches("(?m)^([,]+)$")) {
                        logger.info("***************************Empty CSV Data at line number: {}", count);
                        continue;
                    }

                    if (line.matches(CSV_HORSE_COUNT_PATTERN)) {
                        logger.info("***************************Ignored Horse Count Info at line number: {}", count);
                        continue;
                    }

                    String[] r = readCsvLine(line);

                    String externalId = getCsvCellValue(r, externalIdIndex);
                    String name = getCsvCellValue(r, nameIndex);

                    if (StringUtils.isEmpty(name)) {
                        logger.info("**************************Empty Horse Name: {} at line: {}", name, line);
                        continue;
                    }

                    String rawFoaled = getCsvCellValue(r, foaledIndex);
                    rawFoaled = rawFoaled.split("\\p{Z}")[0];
                    String foaled;
                    if (!isAustraliaFormat && StringUtils.isNotEmpty(rawFoaled)) {
                        foaled = LocalDate.parse(rawFoaled, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
                    } else {
                        foaled = rawFoaled;
                    }

                    String sire = getCsvCellValue(r, sireIndex);
                    String dam = getCsvCellValue(r, damIndex);

                    if (StringUtils.isEmpty(name) && StringUtils.isEmpty(sire) && StringUtils.isEmpty(dam)
                            && StringUtils.isEmpty(rawFoaled)) continue;

                    String color = getCsvCellValue(r, colorIndex);
                    String sex = getCsvCellValue(r, sexIndex);
                    String avatar = getCsvCellValue(r, avatarIndex);

                    String dayHere = getCsvCellValue(r, daysHereIndex);

                    String addedDate = getCsvCellValue(r, addedDateIndex);
                    ;

                    String activeStatus = getCsvCellValue(r, activeStatusIndex);

                    String currentLocation = getCsvCellValue(r, horseLocationIndex);
                    String currentStatus = getCsvCellValue(r, horseStatusIndex);
                    String type = getCsvCellValue(r, typeIndex);
                    String category = getCsvCellValue(r, categoryIndex);
                    String bonusScheme = getCsvCellValue(r, bonusSchemeIndex);
                    String nickName = getCsvCellValue(r, nickNameIndex);

                    // If dayHere is empty, get exportedDate of ownership file. Because of:
                    // When dayHere is empty, usually departed date in horse line of ownership file also empty too.
                    // Maybe we use regex we detect 09/10/2019 as departed date (is wrong):
                    // Absorb ( Redoute's Choice - Mother Flame (NZ)) 17yo Bay Mare      Last served by Sioux Nation on 09/10/2019 - Early Scan
                    // (Normally in ownership file because ownership file and horse file are exported in the same day).
                    // If not in the same day, we have to determine what's horse file exported date is.

                    if (StringUtils.isEmpty(dayHere)) {
                        Set<String> ownershipKeyMap = horseOwnershipMap.keySet();
                        boolean isSameHorseName = ownershipKeyMap.stream().anyMatch(name::equalsIgnoreCase);

                        if (isSameHorseName) {
                            String ownershipAddedDate = horseOwnershipMap.get(name);
                            addedDate = csvExportedDateStr;
                        } else {
                            // Address case addedDate, activeStatus and current location in horse file are empty.
                            // We will face an error if we keep this data intact.
                            if (StringUtils.isEmpty(addedDate) && StringUtils.isEmpty(activeStatus) && StringUtils.isEmpty(currentLocation)) {
                                addedDate = csvExportedDateStr;
                            }
                        }
                    } else {
                        long minusDays = Long.parseLong(dayHere);
                        LocalDate dateAtNewestLocation = LocalDate.parse(csvExportedDateStr, AUSTRALIA_CUSTOM_DATE_FORMAT).minusDays(minusDays);
                        addedDate = dateAtNewestLocation.format(AUSTRALIA_FORMAL_DATE_FORMAT);
                    }

//						if (!addedDate.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
//							logger.info("UNKNOWN TYPE OF ADDED DATE IN HORSE FILE: {} in line: {}", addedDate, addedDate);
//						}

                    horseMap.put(name, addedDate);

                    String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                            StringHelper.csvValue(externalId),
                            StringHelper.csvValue(name),
                            StringHelper.csvValue(foaled),
                            StringHelper.csvValue(sire),
                            StringHelper.csvValue(dam),
                            StringHelper.csvValue(color),
                            StringHelper.csvValue(sex),
                            StringHelper.csvValue(avatar),
                            StringHelper.csvValue(addedDate),
                            StringHelper.csvValue(activeStatus),
                            StringHelper.csvValue(currentLocation),
                            StringHelper.csvValue(currentStatus),
                            StringHelper.csvValue(type),
                            StringHelper.csvValue(category),
                            StringHelper.csvValue(bonusScheme),
                            StringHelper.csvValue(nickName)
                    );
                    builder.append(rowBuilder);
                }

                //compare horse data from horse file and ownerShip file to make sure horse data are exact or not.
                Map<String, String> fromHorseFile = CollectionsHelper.getDiffMap(horseMap, horseOwnershipMap, false);
                Set<String> keyHorse = fromHorseFile.keySet();
                Map<String, String> fromOwnerShipFile = horseOwnershipMap.entrySet().stream()
                        .filter(x -> keyHorse.contains(x.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                result.put("Diff From Horse File", new TreeMap<>(fromHorseFile));
                result.put("Diff From OwnerShip File", new TreeMap<>(fromOwnerShipFile));
            }

            String path = getOutputFolder(dirName) + File.separator + "formatted-horse.csv";
            Files.write(Paths.get(path), builder.toString().getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static <T> Collector<T, ?, T> toSingleton() throws CustomException {
        try {
            return Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                        if (list.size() != 1) {
                            throw new IllegalStateException();
                        }
                        return list.get(0);
                    }
            );
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IllegalStateException) {
                throw new CustomException(
                        new ErrorInfo("Can't detect owner file name. CSV data seemingly a little weird. Please check!"));
            }
            throw e;
        }
    }

    private static String getOutputFolderPath() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return WINDOW_OUTPUT_FILE_PATH;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return UNIX_OUTPUT_FILE_PATH;
        }
        return null;
    }

    private static final String REMOVE_BANK_LINES_PATTERN = "(?m)^[,]*$\n";
    private static final String REMOVE_LINE_BREAK_PATTERN = "\nCT\\b";
    private static final String REMOVE_INVALID_SHARES_PATTERN = "\\bInt.Party\\b";
    //private static final String CORRECT_HORSE_NAME_PATTERN = "(?m)^([^,(]*)(?=\\s\\(.*).*";
    private static final String CORRECT_HORSE_NAME_PATTERN = "(?m)^([^,].*)\\s\\(\\s.*";
    private static final String CORRECT_SHARE_COLUMN_POSITION_PATTERN = "(?m)^,(([\\d]{1,3})(\\.)([\\d]{1,2})%)";
    private static final String TRYING_SHARE_COLUMN_POSITION_PATTERN = "(?m)^(([\\d]{1,3})(\\.)([\\d]{1,2})%)";
    private static final String TRIM_HORSE_NAME_PATTERN = "(?m)^\\s";
    private static final String MOVE_HORSE_TO_CORRECT_LINE_PATTERN = "(?m)^([^,].*)\\n,(?=([\\d]{1,3})?(\\.)?([\\d]{1,2})?%)";
    private static final String IS_INSTANCEOF_DATE_PATTERN = "([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
    private static final String EXTRACT_DEPARTED_DATE_OF_HORSE_PATTERN =
            "(?m)^([^,].*)\\s\\(\\s.*([\\s]+)([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
    private static final String NORMAL_OWNERSHIP_EXPORTED_DATE_PATTERN =
            "(?m)(\\bPrinted\\b[:\\s]+)([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
    
    private static final String ARDEX_OWNERSHIP_EXPORTED_DATE_PATTERN = "(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),\\s" +
            "((\\(0[1-9]|[12][0-9]|3[01])\\s" +
            "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?),\\s" +
            "((19|20)\\d\\d))";

    private static final String IS_DATE_MONTH_YEAR_FORMAT_PATTERN = "^(?:(?:31([/\\-.])(?:0?[13578]|1[02]))\\1|" +
            "(?:(?:29|30)([/\\-.])(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
            "^(?:29([/\\-.])0?2\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
            "^(?:0?[1-9]|1\\d|2[0-8])([/\\-.])(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";

    private static final String IS_MONTH_DATE_YEAR_FORMAT_PATTERN = "^(?:(?:(?:0?[13578]|1[02])([/\\-.])31)\\1|" +
            "(?:(?:0?[13-9]|1[0-2])([/\\-.])(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
            "^(?:0?2([/\\-.])29\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
            "^(?:(?:0?[1-9])|(?:1[0-2]))([/\\-.])(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";

    private static final String MIXING_COMMS_FINANCE_EMAIL_PATTERN = "\"?\\bAccs\\b:\\s((.+) (?=(\\bComms\\b)))\\bComms\\b:\\s((.+)(\\.[a-zA-Z;]+)(?=,))\"?";

    private static final String REMOVE_UNNECESSARY_DATA =
            "(?m)^(?!((,)?Share %)|(.*(?=([\\d]{1,3})(\\.)([\\d]{1,2})%))).*$(\\n)?";
    private static final String EXTRACT_FILE_OWNER_NAME_PATTERN = "(?m)^(Horses)(.+)$(?=\\n)";
    private static final String CSV_HORSE_COUNT_PATTERN = "(?m)^(.+)Horses([,]+)$";
    private static final String OWNERSHIP_HEADER_PATTERN = "(?m)^(.*)?Share %.*$";
    private static final int IGNORED_NON_DATA_LINE_THRESHOLD = 9;
    
    private static final String WINDOW_OUTPUT_FILE_PATH = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\";
    private static final String UNIX_OUTPUT_FILE_PATH = "/home/logbasex/Desktop/data/";
    private static final String CT_IN_DISPLAY_NAME_PATTERN = "\\bCT:";

    private static final DateTimeFormatter AUSTRALIA_CUSTOM_DATE_FORMAT;
    static {
        AUSTRALIA_CUSTOM_DATE_FORMAT = new DateTimeFormatterBuilder()
                .appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
                .appendLiteral('/')
                .appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
                .appendLiteral('/')
                .appendValue(YEAR, 2, 4, SignStyle.NEVER)
                .toFormatter();
    }

    private static final DateTimeFormatter AUSTRALIA_FORMAL_DATE_FORMAT;
    static {
        AUSTRALIA_FORMAL_DATE_FORMAT = new DateTimeFormatterBuilder()
                .appendValue(DAY_OF_MONTH, 2)
                .appendLiteral('/')
                .appendValue(MONTH_OF_YEAR, 2)
                .appendLiteral('/')
                .appendValue(YEAR, 4)
                .toFormatter();
    }

    private static final DateTimeFormatter AMERICAN_CUSTOM_DATE_FORMAT;
    static {
        AMERICAN_CUSTOM_DATE_FORMAT = new DateTimeFormatterBuilder()
                .appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
                .appendLiteral('/')
                .appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
                .appendLiteral('/')
                .appendValue(YEAR, 2, 4, SignStyle.NEVER)
                .toFormatter();
    }
    
    private static final DateTimeFormatter ARDEX_DATE_FORMAT;
    static {
        ARDEX_DATE_FORMAT = new DateTimeFormatterBuilder()
                .parseCaseSensitive()
                .appendPattern("dd MMMM, yyyy")
                .toFormatter();
    }
    
    @Override
    public Map<Object, Object> automateImportOwnerShip(MultipartFile ownershipFile) {
        Map<Object, Object> result = new HashMap<>();

        try {
            List<String> csvData = this.getCsvData(ownershipFile);
            String allLines = String.join("\n", csvData);

            // get file exportedDate.
            // Pattern : ,,,,,,,,,,,,,,Printed: 21/10/2019  3:41:46PM,,,,Page -1 of 1,,,,
            String exportedDate = null;
            Matcher exportedDateMatcher = Pattern.compile(NORMAL_OWNERSHIP_EXPORTED_DATE_PATTERN, Pattern.CASE_INSENSITIVE)
                    .matcher(allLines);

            int exportedDateCount = 0;

            boolean isNormalExportedDate = false;
            while (exportedDateMatcher.find()) {
                exportedDateCount++;

                if (exportedDateCount == 1) {
                    isNormalExportedDate = true;
                    //get date use group(2) of regex.
                    exportedDate = exportedDateMatcher.group(2);

                    // process for case horse file was exported before ownership file was exported.
                    // Using date info in ownership file can cause mismatching in data.
                    // exportedDate = LocalDate.parse(exportedDate, AUSTRALIA_CUSTOM_DATE_FORMAT).minusDays(1).format(AUSTRALIA_CUSTOM_DATE_FORMAT);
                    if (!exportedDate.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
                        throw new CustomException(new ErrorInfo("The exported date was not recognized as a valid Australia format: {}", exportedDate));
                    }

                } else if (exportedDateCount > 1){
                    throw new CustomException(new ErrorInfo("CSV data seems a little weird. Please check!"));
                }
            }
            
            if (!isNormalExportedDate) {
                Matcher ardexExportedDateMatcher = Pattern.compile(ARDEX_OWNERSHIP_EXPORTED_DATE_PATTERN, Pattern.CASE_INSENSITIVE)
                        .matcher(allLines);
                while (ardexExportedDateMatcher.find()) {
                    exportedDate = ardexExportedDateMatcher.group(2);
                    exportedDate = LocalDate.parse(exportedDate, ARDEX_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
                }
            }

            // Line has departedDate likely to extract:
            //Azurite (IRE) ( Azamour (IRE) - High Lite (GB)) 9yo Bay Gelding     Michael Hickmott Bloodstock - In
            //training Michael Hickmott Bloodstock 1/08/2019 >> 1/08/2019
            //This is required for make sure horse data after format csv are exact.

            Matcher departedDateMatcher = Pattern.compile(EXTRACT_DEPARTED_DATE_OF_HORSE_PATTERN).matcher(allLines);

            Map<String, String> horseDataMap = new LinkedHashMap<>();

            while (departedDateMatcher.find()) {
                String horseName = departedDateMatcher.group(1).trim();
                String horseDepartedDate = departedDateMatcher.group(3).trim();

                if (StringUtils.isEmpty(horseName))
                    continue;

                if (StringUtils.isEmpty(horseDepartedDate))
                    logger.info("Horse without departed date: {}", horseName);

                if (!horseDepartedDate.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
                    throw new CustomException(new ErrorInfo("The departed date was not recognized as a valid Australia format: {}", horseDepartedDate));
                }

                //process for case: 25/08/19 (usually 25/08/2019)
                String horseDate = LocalDate.parse(horseDepartedDate, AUSTRALIA_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
                horseDataMap.put(horseName, horseDate);
            }

            result.put("HorseDataMap", horseDataMap);
            result.put("ExportedDate", exportedDate);
            Matcher blankLinesMatcher = Pattern.compile(REMOVE_BANK_LINES_PATTERN).matcher(allLines);
            if (blankLinesMatcher.find()) {
                allLines = allLines.replaceAll(REMOVE_BANK_LINES_PATTERN, StringUtils.EMPTY);
            } else {
                throw new CustomException(ErrorInfo.CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_ERROR);
            }

            //optional
            String lineHasFileOwnerName;
            Matcher extractFileOwnerName = Pattern.compile(EXTRACT_FILE_OWNER_NAME_PATTERN).matcher(allLines);
            if (extractFileOwnerName.find()) {

                logger.info("*******************Lines possible have owner file name:\n {}", extractFileOwnerName.group());

                lineHasFileOwnerName = extractFileOwnerName.group(2);

                if (StringUtils.isEmpty(lineHasFileOwnerName)) {
                    List<String> lineHasFileOwnerNameElements = Arrays.asList(readCsvLine(lineHasFileOwnerName));

                    String fileOwnerName = lineHasFileOwnerNameElements.stream().filter(StringUtils::isNotEmpty)
                            .collect(toSingleton());

                    logger.info("*********************File owner name is : {}", fileOwnerName);
                }

            } else {
                logger.info("*******************Can't detect lines contain owner file name in given file.");
            }


            Matcher linesBreakMatcher = Pattern.compile(REMOVE_LINE_BREAK_PATTERN).matcher(allLines);
            if (linesBreakMatcher.find()) {
                allLines = allLines.replaceAll(REMOVE_LINE_BREAK_PATTERN, " CT");
            } else {
                logger.warn("Cannot apply regex: {}... for ownership file", REMOVE_LINE_BREAK_PATTERN);
            }

            //optional
            Matcher invalidSharesMatcher = Pattern.compile(REMOVE_INVALID_SHARES_PATTERN, Pattern.CASE_INSENSITIVE).matcher(allLines);
            if (invalidSharesMatcher.find()) {
                allLines = allLines.replaceAll(REMOVE_INVALID_SHARES_PATTERN, "0.00%");
            } else {
                logger.warn("Cannot apply regex: {}... for ownership file", REMOVE_INVALID_SHARES_PATTERN);
            }

            Matcher correctHorseNameMatcher = Pattern.compile(CORRECT_HORSE_NAME_PATTERN).matcher(allLines);
            int horseCount = 0;
            while (correctHorseNameMatcher.find()) {
                horseCount++;
            }
            result.put("HorseCount", horseCount);

            //ignore extra data from line contains horse name >> horse name
            //Ambidexter/Elancer 16 ( Ambidexter - Elancer) 3yo Brown Colt     Michael Hickmott Bloodstock - In training Michael Hickmott Bloodstock 24/12/2019 >> Ambidexter/Elancer 16

            Matcher correctShareColumnPosition = Pattern.compile(CORRECT_SHARE_COLUMN_POSITION_PATTERN).matcher(allLines);
            Matcher tryingShareColumnPosition = Pattern.compile(TRYING_SHARE_COLUMN_POSITION_PATTERN).matcher(allLines);
            if (horseCount > 0) {
                //special case: POB-345: Archer park (missing leading comma: normal case these lines don't contain horse name start with ,%share, >> POB-345 start with %share,)
                if (correctShareColumnPosition.find()) {
                    allLines = allLines.replaceAll(CORRECT_HORSE_NAME_PATTERN, "$1");
                } else if (tryingShareColumnPosition.find()) {
                    allLines = allLines.replaceAll(CORRECT_HORSE_NAME_PATTERN, "$1").replaceAll(TRYING_SHARE_COLUMN_POSITION_PATTERN, ",$1");
                } else {
                    logger.warn("Data seemingly weird. Please check!");
                }
            } else {
                throw new CustomException(ErrorInfo.CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_ERROR);
            }

            Matcher trimHorseNameMatcher = Pattern.compile(TRIM_HORSE_NAME_PATTERN).matcher(allLines);
            if (trimHorseNameMatcher.find()) {
                allLines = allLines.replaceAll(TRIM_HORSE_NAME_PATTERN, "");
            } else {
                logger.info("Cannot apply regex: {}... for ownership file", TRIM_HORSE_NAME_PATTERN);
            }

            //Bring horse name and horse data into the same line.
            Matcher correctHorseLinePattern = Pattern.compile(MOVE_HORSE_TO_CORRECT_LINE_PATTERN).matcher(allLines);
            if (correctHorseLinePattern.find()) {
                allLines = allLines.replaceAll(MOVE_HORSE_TO_CORRECT_LINE_PATTERN, "$1,");
            } else {
                throw new CustomException(ErrorInfo.CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_ERROR);
            }

            //remove unnecessary line like:
            // ,,With Share Ownership Information ,,,,,,,,,,,,,,,,,,,,
            Matcher unnecessaryDataMatcher = Pattern.compile(REMOVE_UNNECESSARY_DATA).matcher(allLines);
            if (unnecessaryDataMatcher.find()) {
                allLines = allLines.replaceAll(REMOVE_UNNECESSARY_DATA, "");
            } else {
                logger.warn("Data seemingly weird. Please check!");
            }

            unnecessaryDataMatcher.reset();
            StringBuilder ignoredData = new StringBuilder();
            int gossipDataCount = 0;
            while (unnecessaryDataMatcher.find()) {
                gossipDataCount++;
                ignoredData.append(unnecessaryDataMatcher.group());
            }
            
            logger.info("******************************IGNORED DATA**********************************\n {}", ignoredData);
            //normally unnecessary lines to ignored between 5 and 10.;
            if (gossipDataCount > IGNORED_NON_DATA_LINE_THRESHOLD) {
                throw new CustomException(new ErrorInfo("CSV data seems a little weird. Please check!"));
            }
            
            String[][] data = this.get2DArrayFromString(allLines);
            
            //for case indexOutOfRange exception caused by missing trailing comma in header.
            int headerLength = data[0].length;
            int rowLength = data[1].length;
            String tryingHeader = String.join(",", data[0]);
            if (tryingHeader.matches(OWNERSHIP_HEADER_PATTERN) && (headerLength < rowLength)) {
                IntStream.range(0, rowLength - headerLength).forEach(i -> {
                    data[0] = ArrayUtils.add(data[0], "");
                });
            }
        
            //all possible index of cell has value.
            List<Integer> rowHasValueIndex = new ArrayList<>();

            //all possible index.
            Set<Integer> setAllIndexes = new HashSet<>();

            //all possible index of empty cell.
            Set<Integer> isEmptyIndexes = new HashSet<>();

            // CSV data after using initial regex usually missing these header name: HorseName, AddedDate, GST
            // Can't use regex for file to find column header name addedDate and GST, better we have to find all manually.
            // We need all column data has right header name above to process in the next step.
            int dateIndex = -1;
            int gstIndex = -1;

            //find all cells has empty columns.
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    
                    setAllIndexes.add(j);

                    if (data[i][j].equalsIgnoreCase(StringUtils.EMPTY)) {
                        isEmptyIndexes.add(j);
                    }

                    //append date header
                    if (isRecognizedAsValidDate(data[i][j])) {
                        dateIndex = j;
                        data[0][dateIndex] = "Added Date";
                    }

                    if (data[i][j].equals("N") || data[i][j].equals("Y")) {
                        gstIndex = j;
                    }
                }
            }

            //Append Header
            StringBuilder gstString = new StringBuilder();
            for (String[] row : data) {
                gstString.append(row[gstIndex]);
            }
            
            String distinctGST = gstString.toString().chars().distinct().mapToObj(c -> String.valueOf((char) c)).collect(Collectors.joining());
            if (distinctGST.matches("(YN)|(NY)|N|Y")) {
                data[0][gstIndex] = "GST";
            }

            //Default horse column.
            data[0][0] = "Horse";

            //remains columns always has data in all cells.
            setAllIndexes.removeAll(isEmptyIndexes);

            //find all columns with at least one cell have data, except columns always has data in all cells.
            for (Integer index : isEmptyIndexes) {
                StringBuilder isEmptyString = new StringBuilder();

                for (String[] row : data) {
                    isEmptyString.append(row[index]);
                }

                if (!isEmptyString.toString().equals(StringUtils.EMPTY)) {
                    rowHasValueIndex.add(index);
                }
            }

            //Index of non-empty columns.
            setAllIndexes.addAll(rowHasValueIndex);

            List<Integer> allIndexes = new ArrayList<>(setAllIndexes);

            List<String> csvDataWithBankColumns = this.getListFrom2DArrString(data);

            //write csv data after format original csv file >> ignored completely empty column.
            StringBuilder builder = new StringBuilder();
            for (String line : csvDataWithBankColumns) {
                String[] r = readCsvLine(line);

                StringBuilder rowBuilder = new StringBuilder();

                //write all column has data based on columns index.
                for (Integer index : allIndexes) {
                    rowBuilder.append(r[index]).append(",");
                }
                rowBuilder.append("\n");
                builder.append(rowBuilder);
            }

            String[][] blankHorseNameData = this.get2DArrayFromString(builder.toString());

            //fill empty horse name cells as same as previous cell data.
            for (int i = 1; i < blankHorseNameData.length; ) {
                if (StringUtils.isNotEmpty(blankHorseNameData[i][0])) {
                    for (int j = i + 1; j < blankHorseNameData.length; j++) {
                        if (StringUtils.isNotEmpty(blankHorseNameData[j][0])) {
                            i = j;
                            continue;
                        }
                        blankHorseNameData[j][0] = blankHorseNameData[i][0];
                    }
                }
                i++;
            }

            List<String> csvDataList = this.getListFrom2DArrString(blankHorseNameData);
            StringBuilder dataBuilder = new StringBuilder();

            StringBuilder nameBuilder = new StringBuilder();
            StringBuilder normalNameBuilder = new StringBuilder("\n***********NORMAL NAME***********\n");
            StringBuilder organizationNameBuilder = new StringBuilder("\n***********ORGANIZATION NAME***********\n");

            if (!CollectionUtils.isEmpty(csvDataList)) {

                // ---------- cols of file ownership ---------------------------
                // HORSE KEY (ID or NAME), can leave blank if key is horse name
                // HORSE NAME
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
                // BALANCE (SHARE PERCENTAGE)
                // FROM_DATE
                // TO_DATE
                // EXPORTED_DATE
                // -------------------------------------------------------------

                String[] header = readCsvLine(csvDataList.get(0));

                int horseIdIndex = checkColumnIndex(header, "Horse Id");
                int horseNameIndex = checkColumnIndex(header, "Horse Name", "Horse");
                int ownerIdIndex = checkColumnIndex(header, "Owner Id");
                int commsEmailIndex = checkColumnIndex(header, "CommsEmail", "Email");
                int financeEmailIndex = checkColumnIndex(header, "Finance Email", "FinanceEmail");
                int firstNameIndex = checkColumnIndex(header, "FirstName", "First Name");
                int lastNameIndex = checkColumnIndex(header, "LastName", "Last Name");
                int displayNameIndex = checkColumnIndex(header, "DisplayName", "Name", "Display Name");
                int typeIndex = checkColumnIndex(header, "Type");
                int mobileIndex = checkColumnIndex(header, "Mobile", "Mobile Phone");
                int phoneIndex = checkColumnIndex(header, "Phone");
                int faxIndex = checkColumnIndex(header, "Fax");
                int addressIndex = checkColumnIndex(header, "Address");
                int cityIndex = checkColumnIndex(header, "City");
                int stateIndex = checkColumnIndex(header, "State");
                int postCodeIndex = checkColumnIndex(header, "PostCode");
                int countryIndex = checkColumnIndex(header, "Country");
                int shareIndex = checkColumnIndex(header, "Shares", "Share", "Ownership", "Share %");
                int addedDateIndex = checkColumnIndex(header, "AddedDate", "Added Date");
                int realGstIndex = checkColumnIndex(header, "GST");

                //process file without header
                csvDataList = csvDataList.stream().skip(1).collect(Collectors.toList());

                boolean isAustraliaFormat = isAustraliaFormat(csvDataList, addedDateIndex, "ownership");

                for (String line : csvDataList) {
                    String[] r = readCsvLine(line);

                    String horseId = getCsvCellValue(r, horseIdIndex);
                    String horseName = getCsvCellValue(r, horseNameIndex);
                    String ownerId = getCsvCellValue(r, ownerIdIndex);
                    String commsEmail = getCsvCellValue(r, commsEmailIndex);
                    String financeEmail = getCsvCellValue(r, financeEmailIndex);

			  	        /*
			  	         ### **Process case email cell like:
			  	         Accs: accounts@marshallofbrisbane.com.au Comms:monopoly@bigpond.net.au
			  	         - [1] Extract Comms to communication email cell.
			  	         - [2] Extract Accs to financial email cell.
			  	        */
                    Matcher mixingEmailTypeMatcher = Pattern.compile(MIXING_COMMS_FINANCE_EMAIL_PATTERN, Pattern.CASE_INSENSITIVE).matcher(line);
                    if (mixingEmailTypeMatcher.find()) {

                        String tryingCommsEmail = mixingEmailTypeMatcher.group(4).trim();
                        String tryingFinanceEmail = mixingEmailTypeMatcher.group(2).trim();
                        commsEmail = this.getValidEmailStr(tryingCommsEmail, line);

                        if (StringUtils.isEmpty(financeEmail)) {
                            financeEmail = this.getValidEmailStr(tryingFinanceEmail, line);
                        }
                    } else {
                        commsEmail = this.getValidEmailStr(commsEmail, line);
                        financeEmail = this.getValidEmailStr(financeEmail, line);
                    }

                    String firstName = getCsvCellValue(r, firstNameIndex);
                    String lastName = getCsvCellValue(r, lastNameIndex);
                    String displayName = getCsvCellValue(r, displayNameIndex);
    
                    //We have displayName like "Edmonds Racing CT: Toby Edmonds, Logbasex"
                    //We wanna extract this name to firstName, lastName, displayName:
                    //Any thing before CT is displayName, after is firstName, if after CT contains comma delimiter (,) >> lastName
                    String realDisplayName = this.correctOwnershipName(firstName, lastName, displayName,
                            normalNameBuilder, organizationNameBuilder);

                    String type = getCsvCellValue(r, typeIndex);
                    String mobile = getCsvCellValue(r, mobileIndex);
                    String phone = getCsvCellValue(r, phoneIndex);
                    String fax = getCsvCellValue(r, faxIndex);
                    String address = getCsvCellValue(r, addressIndex);
                    String city = getCsvCellValue(r, cityIndex);
                    String state = getCsvCellValue(r, stateIndex);
                    String postCode = getPostcode(getCsvCellValue(r, postCodeIndex));
                    String country = getCsvCellValue(r, countryIndex);
                    String gst = getCsvCellValue(r, realGstIndex);
                    String share = getCsvCellValue(r, shareIndex);

                    String rawAddedDate = getCsvCellValue(r, addedDateIndex);
                    //remove all whitespace include unicode character
                    rawAddedDate = rawAddedDate.split("\\p{Z}")[0];
                    String addedDate;

                    //convert addedDate read from CSV to Australia date time format.
                    if (!isAustraliaFormat && StringUtils.isNotEmpty(rawAddedDate)) {
                        addedDate = LocalDate.parse(rawAddedDate, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
                    } else {
                        addedDate = rawAddedDate;
                    }

                    String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                            StringHelper.csvValue(horseId),
                            StringHelper.csvValue(horseName),
                            StringHelper.csvValue(ownerId),
                            StringHelper.csvValue(commsEmail),
                            StringHelper.csvValue(financeEmail),
                            StringHelper.csvValue(firstName),
                            StringHelper.csvValue(lastName),
                            StringHelper.csvValue(realDisplayName),
                            StringHelper.csvValue(type),
                            StringHelper.csvValue(mobile),
                            StringHelper.csvValue(phone),
                            StringHelper.csvValue(fax),
                            StringHelper.csvValue(address),
                            StringHelper.csvValue(city),
                            StringHelper.csvValue(state),
                            StringHelper.csvValue(postCode),
                            StringHelper.csvValue(country),
                            StringHelper.csvValue(gst),
                            StringHelper.csvValue(share),
                            StringHelper.csvValue(addedDate),
                            StringHelper.csvValue(exportedDate)
                    );
                    dataBuilder.append(rowBuilder);
                }

                nameBuilder.append(normalNameBuilder).append(organizationNameBuilder);
            }

            result.put("ownershipData", dataBuilder);
            result.put("ownershipName", nameBuilder);
            
            return result;
        } catch (IOException | CustomException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[][] get2DArrayFromString(String value) {
        List<List<String>> nestedListData = Arrays.stream(value.split("\n"))
                .map(StringHelper::customSplitSpecific)
                .collect(Collectors.toList());

        return nestedListData.stream()
                .map(l -> l.toArray(new String[0]))
                .toArray(String[][]::new);
    }

    private List<String> getListFrom2DArrString(String[][] value) {
        List<String> result = new ArrayList<>();
        for (String[] strings : value) {
            String row = String.join(",", strings);
            result.add(row);
        }
        return result;
    }
    
    private String correctOwnershipName(String firstName, String lastName,String displayName, StringBuilder normalNameBuilder,
                                        StringBuilder organizationNameBuilder) {
        
        String realDisplayName = null;
        final List<String> organizationNames = Arrays.asList(
                "Company",
                "Racing",
                "Pty Ltd",
                "Racing Pty Ltd",
                "Breeding",
                "stud",
                "group",
                "bred",
                "breds",
                "tbreds",
                "Thoroughbred",
                "Thoroughbreds",
                "synd",
                "syndicate",
                "syndicates",
                "syndication",
                "syndications",
                "Bloodstock",
                "farm",
                "Horse Transport",
                "Club"
        );
    
        Matcher ctMatcher = Pattern.compile(CT_IN_DISPLAY_NAME_PATTERN, Pattern.CASE_INSENSITIVE).matcher(displayName);
        boolean isOrganizationName =
                organizationNames.stream().anyMatch(name -> displayName.toLowerCase().contains(name.toLowerCase()));
    
        //We have displayName like "Edmonds Racing CT: Toby Edmonds, Logbasex"
        //We wanna extract this name to firstName, lastName, displayName:
        //Any thing before CT is displayName, after is firstName, if after CT contains comma delimiter (,) >> lastName
        if (ctMatcher.find()) {
            if (StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName)) {
                int ctStartedIndex = ctMatcher.start();
                int ctEndIndex = ctMatcher.end();
            
                //E.g: Edmonds Racing
                //for case displayName contains organizationName. Ex: Michael Hickmott Bloodstock CT: Michael Hickmott;
                // >> Convert to format: Michael Hickmott(after CT) - Michael Hickmott Bloodstock(before CT)
                realDisplayName = displayName.substring(0, ctStartedIndex).trim();
                
                //E.g: Toby Edmonds, Logbasex
                String firstAndLastNameStr = displayName.substring(ctEndIndex).trim();
    
                String finalRealDisplayName = realDisplayName;
                boolean isContainsOrganizationName = organizationNames.stream()
                        .anyMatch(name -> finalRealDisplayName.toLowerCase().contains(name.toLowerCase()));
                
                if (isContainsOrganizationName) {
                    realDisplayName = String.join(" - ", firstAndLastNameStr, finalRealDisplayName);
                }
                String[] firstAndLastNameArr = firstAndLastNameStr.split("\\p{Z}");
                if (firstAndLastNameArr.length > 1) {
                    lastName = Arrays.stream(firstAndLastNameArr).reduce((first, second) -> second)
                            .orElse("");
                
                    String finalLastName = lastName;
                    firstName = Arrays.stream(firstAndLastNameArr)
                            .filter(i -> !i.equalsIgnoreCase(finalLastName))
                            .collect(Collectors.joining(StringUtils.SPACE)).trim();
                }
            
                String extractedName = String.format("%s,%s,%s,%s\n",
                        StringHelper.csvValue(displayName),
                        StringHelper.csvValue(realDisplayName),
                        StringHelper.csvValue(firstName),
                        StringHelper.csvValue(lastName)
                );
                normalNameBuilder.append(extractedName);
            }
        
            //case don't include CT in name.
            ctMatcher.reset();
        } else if (!ctMatcher.find() && isOrganizationName) {
            //if displayName is organization name >> keep it intact.
            realDisplayName = displayName;
            firstName = StringUtils.EMPTY;
            lastName = StringUtils.EMPTY;
        
            String extractedName = String.format("%s,%s,%s,%s\n",
                    StringHelper.csvValue(displayName),
                    StringHelper.csvValue(realDisplayName),
                    StringHelper.csvValue(firstName),
                    StringHelper.csvValue(lastName)
            );
            organizationNameBuilder.append(extractedName);
        } else {
            realDisplayName = displayName;
        }
        return realDisplayName;
    }
    
    private boolean isAustraliaFormat(List<String> csvData, int dateIndex, String fileType) {
        boolean isAustraliaFormat = false;

        // MM/DD/YYYY format
        List<String> mdyFormatList = new ArrayList<>();

        // DD/MM/YYYY format
        List<String> ausFormatList = new ArrayList<>();

        for (String line : csvData) {

            if (StringUtils.isEmpty(line)) continue;

            //ignore ,,,,,,,,,,,,,, line.
            if (line.matches("(?m)^([,]+)$")) continue;

            //ignore header.
            if (line.matches(CSV_HORSE_COUNT_PATTERN)) continue;

            String[] r = readCsvLine(line);
            String rawDateTime = getCsvCellValue(r, dateIndex);

            if (StringUtils.isNotEmpty(rawDateTime)) {

                //Process for case: 15/08/2013 15:30
                String date = rawDateTime.split("\\p{Z}")[0];

                if (date.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
                    ausFormatList.add(date);
                } else if (date.matches(IS_MONTH_DATE_YEAR_FORMAT_PATTERN)) {
                    mdyFormatList.add(date);
                } else {
                    logger.info("UNKNOWN TYPE OF DATE IN {} FILE: {} at line : {}", StringUtils.upperCase(fileType), rawDateTime, line);
                }
            }
        }

        // if file contains only one date like: 03/27/2019 >> MM/DD/YYYY format.
        // if all date value in the file have format like: D/M/YYYY format (E.g: 5/6/2020) >> recheck in racingAustralia.horse
        if (CollectionUtils.isEmpty(mdyFormatList) && !CollectionUtils.isEmpty(ausFormatList)) {
            isAustraliaFormat = true;
            logger.info("Type of DATE in {} file is DD/MM/YYY format **OR** M/D/Y format >>>>>>>>> Please check.", StringUtils.upperCase(fileType));

        } else if (!CollectionUtils.isEmpty(mdyFormatList)) {
            logger.info("Type of DATE in {} file is MM/DD/YYY format", StringUtils.upperCase(fileType));

        } else {
            logger.info("Type of DATE in {} file is UNDEFINED", StringUtils.upperCase(fileType));
        }

        return isAustraliaFormat;
    }
    
    private String getValidEmailStr(String emailsStr, String line) throws CustomException {
        if (StringUtils.isEmpty(emailsStr)) return StringUtils.EMPTY;
        String[] emailList = emailsStr.split(";");
        
        for (String email : emailList) {
            if (!StringHelper.isValidEmail(email.trim())) {
                logger.error("*********************Email is invalid: {} at line: {}. Please check!", email, line);
                throw new CustomException(new ErrorInfo("Invalid Email"));
            }
        }
        
        return emailsStr;
    }
    
    private static String getParamValue(String link, String paramName) throws URISyntaxException {
        List<NameValuePair> queryParams = new URIBuilder(link).getQueryParams();
        return queryParams.stream()
                .filter(param -> param.getName().equalsIgnoreCase(paramName))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElse("");
    }
    
    public static void main(String[] args) throws URISyntaxException {
        String strWrap =
                WordUtils.wrap("A really really really really really long sentence.", 50, "\n", false);
        System.out.println(strWrap);
        
        String str = StringUtils.abbreviate("Lala", 4);
        System.out.println(str);
    
//        DateTimeFormatter f = DateTimeFormatter.ofPattern("MMddyyyy");
//        LocalDate bday = null;
//
//        try {`
//            bday = LocalDate.parse(args[0], f);
//        } catch (java.time.DateTimeException e) {
//            System.out.println("bad dates Indy");
//            System.exit(0);
//        }
    
        String url = "https://www.youtube.com/v/VIDEO_ID?version=3&loop=1&playlist=VIDEO_ID";
        Map<Object, Object> params = StringHelper.getRequestParams(url);
        System.out.println(params.get("v"));
        if (params != null) {
            params.forEach((key, value) -> System.out.println(key + " " + value));
        }
        String u = "https://www.youtube.com/watch?v=JgggA8Jtzyg&list=RDJgggA8Jtzyg&start_radio=1";
        System.out.println(getParamValue(u, "v"));
    }
}