package com.logbasex.multithreading.parallel_asynchronous.course.completablefuture;


import com.logbasex.multithreading.parallel_asynchronous.course.service.HelloWorldService;
import com.logbasex.multithreading.parallel_asynchronous.course.util.CommonUtil;
import com.logbasex.multithreading.parallel_asynchronous.course.util.LoggerUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.joining;

public class CompletableFutureHelloWorld {

    private final HelloWorldService hws;

    public CompletableFutureHelloWorld(HelloWorldService helloWorldService) {
        this.hws = helloWorldService;
    }

    public CompletableFuture<String> helloWorld() {

        return CompletableFuture.supplyAsync(hws::helloWorld)//  runs this in a common fork-join pool
                .thenApply(String::toUpperCase);
    }

    public CompletableFuture<String> helloWorld_withSize() {

        return CompletableFuture.supplyAsync(hws::helloWorld)//  runs this in a common fork-join pool
                .thenApply(String::toUpperCase)
                .thenApply((s) -> s.length() + " - " + s);
    }

    public String helloWorld_multiple_async_calls() {
        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world);

        String hw = hello
                .thenCombine(world, (h, w) -> h + w) // (first,second)
                .thenApply(String::toUpperCase)
                .join();

        CommonUtil.timeTaken();

        return hw;
    }


    public String helloWorld_3_async_calls() {
        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world);
        CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return " HI CompletableFuture!";
        });

        String hw = hello
                .thenCombine(world, (h, w) -> h + w) // (first,second)
                .thenCombine(hiCompletableFuture, (previous, current) -> previous + current)
                .thenApply(String::toUpperCase)
                .join();

        CommonUtil.timeTaken();

        return hw;
    }

    public String helloWorld_3_async_calls_log() {
        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world);
        CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return " HI CompletableFuture!";
        });

        String hw = hello
                // .thenCombine(world, (h, w) -> h + w) // (first,second)
                .thenCombine(world, (h, w) -> {
                    LoggerUtil.log("thenCombine h/w ");
                    return h + w;
                }) // (first,second)
                //.thenCombine(hiCompletableFuture, (previous, current) -> previous + current)
                .thenCombine(hiCompletableFuture, (previous, current) -> {
                    LoggerUtil.log("thenCombine , previous/current");
                    return previous + current;
                })
                //.thenApply(String::toUpperCase)
                .thenApply(s -> {
                    LoggerUtil.log("thenApply");
                    return s.toUpperCase();
                })
                .join();

        CommonUtil.timeTaken();

        return hw;
    }

    public String helloWorld_3_async_calls_log_async() {
        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world);
        CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return " HI CompletableFuture!";
        });

        String hw = hello
                // .thenCombine(world, (h, w) -> h + w) // (first,second)
                .thenCombineAsync(world, (h, w) -> {
                    LoggerUtil.log("thenCombine h/w ");
                    return h + w;
                }) // (first,second)
                //.thenCombine(hiCompletableFuture, (previous, current) -> previous + current)
                .thenCombineAsync(hiCompletableFuture, (previous, current) -> {
                    this.hws.hello();
                    LoggerUtil.log("thenCombine , previous/current");
                    return previous + current;
                })
                //.thenApply(String::toUpperCase)
                .thenApplyAsync(s -> {
                    this.hws.hello();
                    LoggerUtil.log("thenApply");
                    return s.toUpperCase();
                })
                .join();

        CommonUtil.timeTaken();

        return hw;
    }


    public String helloWorld_3_async_calls_custom_threadPool() {

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello, executorService);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world, executorService);

        CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return " HI CompletableFuture!";
        }, executorService);

        String hw = hello
                // .thenCombine(world, (h, w) -> h + w) // (first,second)
                .thenCombine(world, (h, w) -> {
                    LoggerUtil.log("thenCombine h/w ");
                    return h + w;
                }) // (first,second)
                //.thenCombine(hiCompletableFuture, (previous, current) -> previous + current)
                .thenCombine(hiCompletableFuture, (previous, current) -> {
                    LoggerUtil.log("thenCombine , previous/current");
                    return previous + current;
                })
                //.thenApply(String::toUpperCase)
                .thenApply(s -> {
                    LoggerUtil.log("thenApply");
                    return s.toUpperCase();
                })
                .join();

        CommonUtil.timeTaken();

        return hw;
    }

    public String helloWorld_3_async_calls_custom_threadpool_async() {

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello, executorService);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world, executorService);

        CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // delay(1000);
            return " HI CompletableFuture!";
        }, executorService);

        String hw = hello
                // .thenCombine(world, (h, w) -> h + w) // (first,second)
                .thenCombineAsync(world, (h, w) -> {
                    LoggerUtil.log("thenCombine h/w ");
                    return h + w;
                }, executorService) // (first,second)

                /*  .thenCombineAsync(world, (h, w) -> {
                      log("thenCombine h/w ");
                      return h + w;
                  }) // with no executor service as an input*/
                //.thenCombine(hiCompletableFuture, (previous, current) -> previous + current)
                .thenCombineAsync(hiCompletableFuture, (previous, current) -> {
                    LoggerUtil.log("thenCombine , previous/current");
                    return previous + current;
                }, executorService)
                //.thenApply(String::toUpperCase)
                .thenApply(s -> {
                    LoggerUtil.log("thenApply");
                    return s.toUpperCase();
                })
                .join();

        CommonUtil.timeTaken();

        return hw;
    }


    public String helloWorld_4_async_calls() {
        CommonUtil.startTimer();
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(this.hws::hello);
        CompletableFuture<String> world = CompletableFuture.supplyAsync(this.hws::world);
        CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return " HI CompletableFuture!";
        });
        CompletableFuture<String> byeCompletableFuture = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return " Bye!";
        });


        String hw = hello
                .thenCombine(world, (h, w) -> h + w) // (first,second)
                .thenCombine(hiCompletableFuture, (previous, current) -> previous + current)
                .thenCombine(byeCompletableFuture, (previous, current) -> previous + current)
                .thenApply(String::toUpperCase)
                .join();

        CommonUtil.timeTaken();

        return hw;
    }

    public CompletableFuture<String> helloWorld_thenCompose() {
    
        return CompletableFuture.supplyAsync(this.hws::hello)
                .thenCompose(hws::worldFuture)
                //.thenApply(previous -> helloWorldService.worldFuture(previous))
                .thenApply(String::toUpperCase);

    }

    public String allOf() {
        CommonUtil.startTimer();

        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            return "Hello";
        });

        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(2000);
            return " World";
        });

        List<CompletableFuture<String>> cfList = List.of(cf1, cf2);
        CompletableFuture<Void> cfAllOf = CompletableFuture.allOf(cfList.toArray(new CompletableFuture[cfList.size()]));
        String result = cfAllOf.thenApply(v -> cfList.stream()
                .map(CompletableFuture::join)
                .collect(joining())).join();

        CommonUtil.timeTaken();

        return result;

    }

    public String anyOf() {
        CommonUtil.startTimer();

        CompletableFuture<String> db = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(1000);
            LoggerUtil.log("response from db");
            return "Hello World";
        });

        CompletableFuture<String> restApi = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(2000);
            LoggerUtil.log("response from restApi");
            return "Hello World";
        });

        CompletableFuture<String> soapApi = CompletableFuture.supplyAsync(() -> {
            CommonUtil.delay(3000);
            LoggerUtil.log("response from soapApi");
            return "Hello World";
        });

        List<CompletableFuture<String>> cfList = List.of(db, restApi, soapApi);
        CompletableFuture<Object> cfAllOf = CompletableFuture.anyOf(cfList.toArray(new CompletableFuture[cfList.size()]));
        String result =  (String) cfAllOf.thenApply(v -> {
            if (v instanceof String) {
                return v;
            }
            return null;
        }).join();

        CommonUtil.timeTaken();
        return result;
    }


    public String helloWorld_1() {

        return CompletableFuture.supplyAsync(hws::helloWorld)//  runs this in a common fork-join pool
                .thenApply(String::toUpperCase)
                .join();

    }

    public CompletableFuture<String> complete(String input) {

        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture = completableFuture
                .thenApply(String::toUpperCase)
                .thenApply((result) -> result.length() + " - " + result);

        completableFuture.complete(input);

        return completableFuture;

    }

    public static void main(String[] args) {

        HelloWorldService helloWorldService = new HelloWorldService();
        CompletableFuture.supplyAsync(helloWorldService::helloWorld) //  runs this in a common fork-join pool
                .thenApply(String::toUpperCase)
                .thenAccept((result) -> LoggerUtil.log("result " + result))
                
                // join block the caller thread (main thread) util computation is completed.
                .join();

        LoggerUtil.log("Done!");
        //blocking the main thread for 2 second.
        CommonUtil.delay(2000);
    }
}
