package com.logbasex.aop.agent;
/**
 * <a href="https://programmer.group/the-difference-between-static-agent-and-dynamic-agent.html"></a>
 * <a href="https://topic.alibabacloud.com/a/spring-static-agents-and-dynamic-agents_8_8_30312760.html"></a>
 */
public class Main {
    public static void main(String[] args) {
        StaticAgentHandler staticAgent = new StaticAgentHandler(new RealObject());
//        staticAgent.doSomething();

        RealObject realObject = new RealObject();
        Action dynamicAgent = (Action) java.lang.reflect.Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{Action.class},
                new JdkDynamicAgentHandler(realObject)
        );
//        dynamicAgent.doSomething();

        CglibDynamicAgentHandler cglibDynamicAgent = new CglibDynamicAgentHandler();
        RealObject instance = (RealObject) cglibDynamicAgent.getInstance(realObject);
        instance.doSomething();
    }
}
