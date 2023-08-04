package com.logbasex.multithreading.parallel_asynchronous.course.util;

public class LoggerUtil {

    public static void log(String message){

        System.out.println("[" + Thread.currentThread().getName() +"] - " + message);

    }
}
