package com.logbasex.multithreading.parallel_asynchronous.course.thread;

import com.logbasex.multithreading.parallel_asynchronous.course.domain.Product;
import com.logbasex.multithreading.parallel_asynchronous.course.domain.ProductInfo;
import com.logbasex.multithreading.parallel_asynchronous.course.domain.Review;
import com.logbasex.multithreading.parallel_asynchronous.course.service.ProductInfoService;
import com.logbasex.multithreading.parallel_asynchronous.course.service.ReviewService;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;
import lombok.RequiredArgsConstructor;

import static com.logbasex.multithreading.parallel_asynchronous.course.util.LoggerUtil.log;

@RequiredArgsConstructor
public class ProductServiceUsingThread {
    private final ProductInfoService productInfoService;
    private final ReviewService reviewService;

    public Product retrieveProductDetails(String productId) throws InterruptedException {
        CommonUtil.stopWatch.start();
        ProductInfoRunnable productInfoRunnable = new ProductInfoRunnable(productId);
        ReviewRunnable reviewRunnable = new ReviewRunnable(productId);

        Thread productInfoThread = new Thread(productInfoRunnable);
        Thread reviewThread = new Thread(reviewRunnable);

        productInfoThread.start();
        reviewThread.start();

        productInfoThread.join();
        reviewThread.join();

        CommonUtil.stopWatch.stop();
        log("Total Time Taken : "+ CommonUtil.stopWatch.getTime());
        return new Product(productId, productInfoRunnable.productInfo, reviewRunnable.review);
    }

    public static void main(String[] args) throws InterruptedException {
        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();
        ProductServiceUsingThread productService = new ProductServiceUsingThread(productInfoService, reviewService);
        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        log("Product is " + product);
    }

    private class ProductInfoRunnable implements Runnable {
        private final String productId;
        private ProductInfo productInfo;

        public ProductInfo getProductInfo() {
            return productInfo;
        }

        public ProductInfoRunnable(String productId) {
            this.productId = productId;
        }

        @Override
        public void run() {
            productInfo = productInfoService.retrieveProductInfo(productId);
        }
    }

    private class ReviewRunnable implements Runnable {
        private final String productId;
        private Review review;
        public ReviewRunnable(String productId) {
            this.productId = productId;
        }
        public Review getReview() {
            return review;
        }
        @Override
        public void run() {
            review = reviewService.retrieveReviews(productId);
        }
    }
}
