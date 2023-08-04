package com.logbasex.multithreading.parallel_asynchronous.course.service;

import com.logbasex.multithreading.parallel_asynchronous.course.domain.checkout.CartItem;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;
import com.logbasex.multithreading.parallel_asynchronous.course.util.LoggerUtil;

public class PriceValidatorService {

    public boolean isCartItemInvalid(CartItem cartItem){
        int cartId = cartItem.getItemId();
        LoggerUtil.log("isCartItemInvalid : "+ cartItem);
        CommonUtil.delay(500);
        return cartId == 7 || cartId == 9 || cartId == 11;
    }
}
