package com.logbasex.agent;

import java.lang.instrument.Instrumentation;

public class MyFirstAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Start!");
    }

    //https://stackoverflow.com/questions/19786078/what-is-the-use-of-agentmain-method-in-java-instrumentation
    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("...");
        premain(agentArgs, inst);
    }
}
