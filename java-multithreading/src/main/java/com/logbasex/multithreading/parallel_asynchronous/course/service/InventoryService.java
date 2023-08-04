package com.logbasex.multithreading.parallel_asynchronous.course.service;

import com.logbasex.multithreading.parallel_asynchronous.course.domain.Inventory;
import com.logbasex.multithreading.parallel_asynchronous.course.domain.ProductOption;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;

public class InventoryService {
    public Inventory retrieveInventory(ProductOption productOption) {
        CommonUtil.delay(500);
        return Inventory
                .builder()
                .count(2).build();

    }
}
