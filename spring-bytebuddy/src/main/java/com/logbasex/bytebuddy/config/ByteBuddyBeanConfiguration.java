package com.logbasex.bytebuddy.config;

import com.logbasex.bytebuddy.aop.ByteBuddyProxyFactoryBean;
import com.logbasex.bytebuddy.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <a href="http://javainsimpleway.com/configuring-spring-beans-without-xml/">...</a>
 * <a href="https://github.com/anegrin/bytebuddy-spring/blob/master/src/main/resources/applicationContext.xml">...</a>
 * <a href="https://stackoverflow.com/questions/58590635/how-do-i-convert-an-applicationcontext-xml-into-a-spring-configuration-class">...</a>
 */
@Configuration
public class ByteBuddyBeanConfiguration {

    @Bean
    @Primary
    public ByteBuddyProxyFactoryBean byteBuddyProxyFactoryBean(@Autowired HelloService helloSv){
        ByteBuddyProxyFactoryBean byteBuddyProxyFactoryBean = new ByteBuddyProxyFactoryBean();
        helloSv.setHelloPrefix("hello");
        byteBuddyProxyFactoryBean.setTarget(helloSv);
        return byteBuddyProxyFactoryBean;
    }
}
