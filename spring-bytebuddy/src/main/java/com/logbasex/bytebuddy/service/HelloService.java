package com.logbasex.bytebuddy.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class HelloService {

    private static final Random random = new Random();

    private String helloPrefix;

    /**
     * set a hello message prefix (to verify the instance to be the good one :D
     * )
     *
     * @param helloPrefix a stupid message :D
     */
    public void setHelloPrefix(String helloPrefix) {
        this.helloPrefix = helloPrefix;
    }

    /**
     * sleep randomly and return the message
     *
     * @return helloPrefix+" "+sleep
     */
    public String get() {

        int sleep = random.nextInt(500);
        try {
            Thread.sleep(sleep);
        } catch (Throwable ignored) {
        }

        return helloPrefix + " " + sleep;
    }

}
