package com.logbasex.agent;

import java.lang.instrument.Instrumentation;

public class MyFirstAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Start!");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("...");
        premain(agentArgs, inst);
    }
}
