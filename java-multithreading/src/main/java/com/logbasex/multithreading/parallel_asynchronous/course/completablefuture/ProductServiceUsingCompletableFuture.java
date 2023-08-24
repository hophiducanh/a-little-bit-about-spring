package com.logbasex.multithreading.parallel_asynchronous.course.completablefuture;


import com.logbasex.multithreading.parallel_asynchronous.course.domain.*;
import com.logbasex.multithreading.parallel_asynchronous.course.service.InventoryService;
import com.logbasex.multithreading.parallel_asynchronous.course.service.ProductInfoService;
import com.logbasex.multithreading.parallel_asynchronous.course.service.ReviewService;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;
import com.logbasex.multithreading.parallel_asynchronous.course.util.LoggerUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public class ProductServiceUsingCompletableFuture {
    private final ProductInfoService productInfoService;
    private final ReviewService reviewService;
    private InventoryService inventoryService;

    public ProductServiceUsingCompletableFuture(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
    }

    public ProductServiceUsingCompletableFuture(ProductInfoService productInfoService, ReviewService reviewService, InventoryService inventoryService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
        this.inventoryService = inventoryService;
    }

    public Product retrieveProductDetails(String productId) {

        CommonUtil.startTimer();
        CompletableFuture<ProductInfo> cfProductInfo = CompletableFuture.supplyAsync(() -> productInfoService.retrieveProductInfo(productId));
        CompletableFuture<Review> cfReview = CompletableFuture.supplyAsync(() -> reviewService.retrieveReviews(productId));

        Product product = cfProductInfo
                .thenCombine(cfReview, (productInfo, review) -> new Product(productId, productInfo, review))
                .join(); // blocks the thread
        CommonUtil.timeTaken();
        return product;
    }

    public CompletableFuture<Product> retrieveProductDetails_CF(String productId) {

        CompletableFuture<ProductInfo> cfProductInfo = CompletableFuture.supplyAsync(() -> productInfoService.retrieveProductInfo(productId));
        CompletableFuture<Review> cfReview = CompletableFuture.supplyAsync(() -> reviewService.retrieveReviews(productId));

        return cfProductInfo
                .thenCombine(cfReview, (productInfo, review) -> new Product(productId, productInfo, review));
    }


    public Product retrieveProductDetailsWithInventory(String productId) {

        CommonUtil.startTimer();
        CompletableFuture<ProductInfo> cfProductInfo = CompletableFuture.supplyAsync(() -> productInfoService.retrieveProductInfo(productId))
                .thenApply((productInfo -> {
                    productInfo.setProductOptions(updateInventoryToProductOption(productInfo));
                    return productInfo;
                }))
                .handle(((productInfo, throwable) -> {
                    LoggerUtil.log("productInfo : " + productInfo);
                    LoggerUtil.log("throwable : " + throwable);
                    return productInfo;
                }));

        CompletableFuture<Review> cfReview = CompletableFuture.supplyAsync(() -> reviewService.retrieveReviews(productId));

        Product product = cfProductInfo
                .thenCombine(cfReview, (productInfo, review) -> new Product(productId, productInfo, review))
                .join(); // blocks the thread
        CommonUtil.timeTaken();
        return product;
    }

    public Product retrieveProductDetailsWithInventory_approach2(String productId) {

        CommonUtil.startTimer();
        CompletableFuture<ProductInfo> cfProductInfo = CompletableFuture.supplyAsync(() -> productInfoService.retrieveProductInfo(productId))
                .thenApply((productInfo -> {
                    productInfo.setProductOptions(updateInventoryToProductOption_approach2(productInfo));
                    //  productInfo.setProductOptions(updateInventoryToProductOption_approach3(productInfo));
                    return productInfo;
                }));

        CompletableFuture<Review> cfReview = CompletableFuture.supplyAsync(() -> reviewService.retrieveReviews(productId))
                .exceptionally((ex) -> {
                    LoggerUtil.log("Handled the Exception in review Service : " + ex.getMessage());
                    return Review
                            .builder()
                            .noOfReviews(0).overallRating(0.0)
                            .build();
                });

        Product product = cfProductInfo
                .thenCombine(cfReview, (productInfo, review) -> new Product(productId, productInfo, review))
                .whenComplete((prod, ex) -> {
                    LoggerUtil.log("Inside whenComplete : " + prod + "and the exception is " + ex);
                    if (ex != null) {
                        LoggerUtil.log("Exception in whenComplete is : " + ex);
                    }
                })
                .join(); // blocks the thread
        CommonUtil.timeTaken();
        return product;
    }


    private List<ProductOption> updateInventoryToProductOption(ProductInfo productInfo) {
    
        return productInfo.getProductOptions()
                .stream()
                .peek(productOption -> {
                    Inventory inventory = inventoryService.retrieveInventory(productOption);
                    productOption.setInventory(inventory);
                })
                .collect(Collectors.toList());
    }

    private List<ProductOption> updateInventoryToProductOption_approach2(ProductInfo productInfo) {

        List<CompletableFuture<ProductOption>> productOptionList = productInfo.getProductOptions()
                .stream()
                .map(productOption ->
                        CompletableFuture.supplyAsync(() -> inventoryService.retrieveInventory(productOption))
                                .exceptionally((ex) -> {
                                    LoggerUtil.log("Exception in Inventory Service : " + ex.getMessage());
                                    return Inventory.builder()
                                            .count(1).build();
                                })
                                .thenApply((inventory -> {
                                    productOption.setInventory(inventory);
                                    return productOption;
                                })))
                .collect(Collectors.toList());

        return productOptionList.stream().map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private List<ProductOption> updateInventoryToProductOption_approach3(ProductInfo productInfo) {

        List<CompletableFuture<ProductOption>> productOptionList = productInfo.getProductOptions()
                .stream()
                .map(productOption ->
                        CompletableFuture.supplyAsync(() -> inventoryService.retrieveInventory(productOption))
                                .exceptionally((ex) -> {
                                    LoggerUtil.log("Exception in Inventory Service : " + ex.getMessage());
                                    return Inventory.builder()
                                            .count(1).build();
                                })
                                .thenApply((inventory -> {
                                    productOption.setInventory(inventory);
                                    return productOption;
                                })))
                .collect(Collectors.toList());

        CompletableFuture<Void> cfAllOf = CompletableFuture.allOf(productOptionList.toArray(new CompletableFuture[productOptionList.size()]));
        return cfAllOf
                .thenApply(v-> productOptionList.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                .join();

    }

    public static void main(String[] args) {

        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();
        ProductServiceUsingCompletableFuture productService = new ProductServiceUsingCompletableFuture(productInfoService, reviewService);
        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        LoggerUtil.log("Product is " + product);
    }
}
