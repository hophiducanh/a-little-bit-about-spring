package com.logbasex.multithreading.parallel_asynchronous.course.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @NonNull
    private String productId;
    @NonNull
    private ProductInfo productInfo;
    @NonNull
    private Review review;
}
