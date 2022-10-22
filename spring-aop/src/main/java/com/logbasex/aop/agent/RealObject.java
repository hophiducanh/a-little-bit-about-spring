package com.logbasex.aop.agent;

public class RealObject implements Action {

    @Override
    public void doSomething() {
        System.out.println("do something");
    }
}
