package com.tellyouiam.alittlebitaboutspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.tellyouiam")
@EnableCaching
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ALittleBitAboutSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ALittleBitAboutSpringApplication.class, args);
    }

}
