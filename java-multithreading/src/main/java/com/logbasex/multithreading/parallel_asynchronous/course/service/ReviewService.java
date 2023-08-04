package com.logbasex.multithreading.parallel_asynchronous.course.service;

import com.logbasex.multithreading.parallel_asynchronous.course.domain.Review;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;
import com.logbasex.multithreading.parallel_asynchronous.course.util.LoggerUtil;

public class ReviewService {

    public Review retrieveReviews(String productId) {
        CommonUtil.delay(1000);
        LoggerUtil.log("retrieveReviews after Delay");
        return new Review(200, 4.5);
    }
}
