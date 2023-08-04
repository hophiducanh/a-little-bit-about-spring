package com.logbasex.multithreading.parallel_asynchronous.course.service;

import com.logbasex.multithreading.parallel_asynchronous.course.domain.Product;
import com.logbasex.multithreading.parallel_asynchronous.course.domain.ProductInfo;
import com.logbasex.multithreading.parallel_asynchronous.course.domain.Review;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;

import static com.logbasex.multithreading.parallel_asynchronous.course.util.LoggerUtil.log;

public class ProductService {
    private ProductInfoService productInfoService;
    private ReviewService reviewService;

    public ProductService(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
    }

    public Product retrieveProductDetails(String productId) {
        CommonUtil.stopWatch.start();

        ProductInfo productInfo = productInfoService.retrieveProductInfo(productId); // blocking call
        Review review = reviewService.retrieveReviews(productId); // blocking call

        CommonUtil.stopWatch.stop();
        log("Total Time Taken : "+ CommonUtil.stopWatch.getTime());
        return new Product(productId, productInfo, review);
    }

    public static void main(String[] args) {

        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();
        ProductService productService = new ProductService(productInfoService, reviewService);
        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        log("Product is " + product);
    }
}
