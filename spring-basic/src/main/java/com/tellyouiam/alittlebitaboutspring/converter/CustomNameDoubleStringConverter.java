package com.tellyouiam.alittlebitaboutspring.converter;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class CustomNameDoubleStringConverter extends AbstractBeanField<Double> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        //if (StringUtils.isEmpty(value)) return StringUtils.EMPTY;
        String doubleValueStr = value.replace("$","").replaceAll(",","");
        Double cellValue = Double.parseDouble(doubleValueStr);
        return cellValue;
    }
}
