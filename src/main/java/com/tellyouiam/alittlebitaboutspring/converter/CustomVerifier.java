package com.tellyouiam.alittlebitaboutspring.converter;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.tellyouiam.alittlebitaboutspring.dto.csvformat.Horse;

public class CustomVerifier implements BeanVerifier<Horse> {

    @Override
    public boolean verifyBean(Horse bean) throws CsvConstraintViolationException {
        return false;
    }
}
