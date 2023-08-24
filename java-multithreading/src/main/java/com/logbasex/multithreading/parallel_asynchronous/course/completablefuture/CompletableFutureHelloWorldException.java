package com.logbasex.multithreading.parallel_asynchronous.course.completablefuture;


import com.logbasex.multithreading.parallel_asynchronous.course.service.HelloWorldService;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;
import com.logbasex.multithreading.parallel_asynchronous.course.util.LoggerUtil;

import java.util.concurrent.CompletableFuture;


public class CompletableFutureHelloWorldException {

    private final HelloWorldService hws;

    public CompletableFutureHelloWorldException(HelloWorldService helloWorldService) {
        this.hws = helloWorldService;
    }

    public String helloWorld_3_async_calls_handle() {
        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world);
        CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return " HI CompletableFuture!";
        });

        String hw = hello
                .handle((result, e) -> { // this gets invoked for both success and failure
                    LoggerUtil.log("result is : " + result);
                    if (e != null) {
                        LoggerUtil.log("Exception is : " + e.getMessage());
                        return "";
                    }
                    return result;

                })
                .thenCombine(world, (h, w) -> h + w) // (first,second)
                .handle((result, e) -> { // this gets invoked for both success and failure
                    LoggerUtil.log("result is : " + result);
                    if (e != null) {
                        LoggerUtil.log("Exception Handle after world : " + e.getMessage());
                        return "";
                    }
                    return result;
                })
                .thenCombine(hiCompletableFuture, (previous, current) -> previous + current)
                .thenApply(String::toUpperCase)

                .join();

        CommonUtil.timeTaken();

        return hw;
    }

    public String helloWorld_3_async_calls_exceptionally() {
        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world);
        CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return " HI CompletableFuture!";
        });

        String hw = hello
                .exceptionally((e) -> { // this gets invoked for both success and failure
                        LoggerUtil.log("Exception is : " + e.getMessage());
                    return "";
                })
                .thenCombine(world, (h, w) -> h + w) // (first,second)
                .exceptionally((e) -> { // this gets invoked for both success and failure
                        LoggerUtil.log("Exception Handle after world : " + e.getMessage());
                        return "";
                })
                .thenCombine(hiCompletableFuture, (previous, current) -> previous + current)
                .thenApply(String::toUpperCase)

                .join();

        CommonUtil.timeTaken();

        return hw;
    }


    public String helloWorld_3_async_whenComplete() {
        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world);
        CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return " HI CompletableFuture!";
        });

        String hw = hello
                .whenComplete((result, e) -> { // this gets invoked for both success and failure
                    LoggerUtil.log("result is : " + result);
                    if (e != null) {
                        LoggerUtil.log("Exception is : " + e.getMessage());
                    }
                })
                .thenCombine(world, (h, w) -> h + w) // (first,second)
                .whenComplete((result, e) -> { // this gets invoked for both success and failure
                    LoggerUtil.log("result is : " + result);
                    if (e != null) {
                        LoggerUtil.log("Exception Handle after world : " + e.getMessage());
                    }
                })
                .exceptionally((e) -> { // this gets invoked for both success and failure
                    LoggerUtil.log("Exception Handle after world : " + e.getMessage());
                    return "";
                })
                .thenCombine(hiCompletableFuture, (previous, current) -> previous + current)
                .thenApply(String::toUpperCase)

                .join();

        CommonUtil.timeTaken();
        return hw;
    }

}
