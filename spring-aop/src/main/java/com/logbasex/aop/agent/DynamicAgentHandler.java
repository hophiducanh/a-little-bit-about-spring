package com.logbasex.aop.agent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicAgentHandler implements InvocationHandler {

    private final Object realObject;

    public DynamicAgentHandler(Object realObject) {
        this.realObject = realObject;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //Agent extension logic
        System.out.print("proxy do: ");

        return method.invoke(realObject, args);
    }
}
