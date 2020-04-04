package com.tellyouiam.alittlebitaboutspring.converter;

import com.opencsv.bean.AbstractCsvConverter;
import org.apache.commons.lang3.StringUtils;

public class CustomJoinNameDoubleStringConverter extends AbstractCsvConverter {

    @Override
    public Object convertToRead(String value) {
        if (StringUtils.isEmpty(value)) return (double) 0;
        String doubleValueStr = value.replace("$","").replaceAll(",","");
        Double cellValue = Double.parseDouble(doubleValueStr);
        return cellValue;
    }
}
