package com.tellyouiam.alittlebitaboutspring.converter;

import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class CustomJoinNameDoubleStringConverter extends AbstractCsvConverter {

    @Override
    public Object convertToRead(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        String doubleValueStr = value.replace("$","").replaceAll(",","");
        Double cellValue = Double.parseDouble(doubleValueStr);
        return cellValue;
    }
}
