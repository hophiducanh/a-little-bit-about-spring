package com.tellyouiam.alittlebitaboutspring;

import com.tellyouiam.alittlebitaboutspring.config.MySQLConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

//@EnableAutoConfiguration(exclude = { //
//        DataSourceAutoConfiguration.class, //
//        DataSourceTransactionManagerAutoConfiguration.class, //
//        HibernateJpaAutoConfiguration.class })
// a combination of three annotations: @Configuration, which is used in Java-based configuration on Spring framework,
// @ComponentScan to enable component scanning of components you write like @Controller classes, and
// @EnableAutoConfgiuration itself, which is used to allow for auto-configuration in Spring Boot application.
//
//Read more: https://www.java67.com/2018/05/difference-between-springbootapplication-vs-EnableAutoConfiguration-annotations-Spring-Boot.html#ixzz6ipJ2OyUP
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableCaching
@Import({MySQLConfig.class})
public class ALittleBitAboutSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ALittleBitAboutSpringApplication.class, args);
    }

}
