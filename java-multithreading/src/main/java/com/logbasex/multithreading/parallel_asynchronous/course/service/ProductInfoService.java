package com.logbasex.multithreading.parallel_asynchronous.course.service;

import com.logbasex.multithreading.parallel_asynchronous.course.domain.ProductInfo;
import com.logbasex.multithreading.parallel_asynchronous.course.domain.ProductOption;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;
import com.logbasex.multithreading.parallel_asynchronous.course.util.LoggerUtil;

import java.util.List;


public class ProductInfoService {

    public ProductInfo retrieveProductInfo(String productId) {
        CommonUtil.delay(1000);
        List<ProductOption> productOptions = List.of(new ProductOption(1, "64GB", "Black", 699.99),
                                                     new ProductOption(2, "128GB", "Black", 749.99));
        LoggerUtil.log("retrieveProductInfo after Delay");
        return ProductInfo
                .builder().productId(productId)
                .productOptions(productOptions)
                .build();
    }
}
