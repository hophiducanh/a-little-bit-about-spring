package com.logbasex.bytebuddy.controller;

import com.logbasex.bytebuddy.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private HelloService helloSv;

    /**
     * map /
     *
     * @return the message from the hello service
     */
    @GetMapping("/")
    public String index() {
        return "Got " + helloSv.get() + " from an instanceof " + helloSv;
    }
}
