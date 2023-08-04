package com.logbasex.multithreading.parallel_asynchronous.course.service;

import com.logbasex.multithreading.parallel_asynchronous.course.domain.checkout.Cart;
import com.logbasex.multithreading.parallel_asynchronous.course.domain.checkout.CartItem;
import com.logbasex.multithreading.parallel_asynchronous.course.domain.checkout.CheckoutResponse;
import com.logbasex.multithreading.parallel_asynchronous.course.domain.checkout.CheckoutStatus;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;
import com.logbasex.multithreading.parallel_asynchronous.course.util.LoggerUtil;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CheckoutService {

    private PriceValidatorService priceValidatorService;

    public CheckoutService(PriceValidatorService priceValidatorService) {
        this.priceValidatorService = priceValidatorService;
    }

    public CheckoutResponse checkout(Cart cart) {

        CommonUtil.startTimer();
        List<CartItem> priceValidationList = cart.getCartItemList()
                //.stream()
                .parallelStream()
                .peek(cartItem -> {
                    boolean isPriceValid = priceValidatorService.isCartItemInvalid(cartItem);
                    cartItem.setExpired(isPriceValid);
                })
                .filter(CartItem::isExpired)
                .collect(toList());
        CommonUtil.timeTaken();
        CommonUtil.stopWatchReset();

        if (priceValidationList.size() > 0) {
            LoggerUtil.log("Checkout Error");
            return new CheckoutResponse(CheckoutStatus.FAILURE, priceValidationList);
        }

        //double finalRate = calculateFinalPrice(cart);
        double finalRate = calculateFinalPrice_reduce(cart);
        LoggerUtil.log("Checkout Complete and the final rate is " + finalRate);

        return new CheckoutResponse(CheckoutStatus.SUCCESS, finalRate);
    }

    private double calculateFinalPrice(Cart cart) {
        return cart
                .getCartItemList()
                .parallelStream()
                .map(cartItem -> cartItem.getQuantity() * cartItem.getRate())
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double calculateFinalPrice_reduce(Cart cart) {
        return cart.getCartItemList()
                .parallelStream()
                .map(cartItem -> cartItem.getQuantity() * cartItem.getRate())
                //.reduce(0.0, (x,y)->x+y);
                .reduce(0.0, Double::sum);
        //Identity for multiplication is 1
        //Identity for addition  is 0
    }
}
