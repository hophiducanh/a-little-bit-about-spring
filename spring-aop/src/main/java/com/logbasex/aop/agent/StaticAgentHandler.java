package com.logbasex.aop.agent;

public class StaticAgentHandler implements Action {

    private final Action realObject;

    public StaticAgentHandler(Action realObject) {
        this.realObject = realObject;
    }

    @Override
    public void doSomething() {
        System.out.print("proxy do: ");
        realObject.doSomething();
    }
}
