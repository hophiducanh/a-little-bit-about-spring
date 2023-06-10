package com.logbasex.bytebuddy.aop;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Method;

public class ByteBuddyProxyFactoryBean implements FactoryBean<Object> {

    private static final Log log = LogFactory.getLog(ByteBuddyProxyFactoryBean.class);

    private boolean singleton = true;

    private Object target;

    private Object instance;

    /**
     * set the target bean
     *
     * @param target the target bean
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * get the instance to be published in spring context
     *
     * @return the bean instance
     */
    @Override
    public Object getObject() {
        if (singleton) {
            return getInstance();
        } else {
            return newInstance();
        }
    }

    /**
     * type of the bean
     *
     * @return the type of the set target
     */
    @Override
    public Class<?> getObjectType() {
        return target != null ? target.getClass() : null;
    }

    /**
     * return if the bean has to be a singleton
     *
     * @return if the bean has to be a singleton
     *
     */
    @Override
    public boolean isSingleton() {
        return singleton;
    }

    /**
     * set if singleton
     *
     * @param singleton if singleton
     */
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * create a new instance: using ByteBuddy to create a subclass and
     * intercepting all methods declared in the "superclass"
     *
     * @return an extended instance of target
     */
    private Object newInstance() {

        try {
            Class<?> targetClass = target.getClass();
            Class<?> proxyType = new ByteBuddy()
                    .subclass(targetClass)
                    .name(targetClass.getName() + "Subclass")
                    //.method(ElementMatchers.any())//if you want to match anything
                    .method(ElementMatchers.isDeclaredBy(targetClass))
                    .intercept(MethodDelegation.to(new Interceptor(target)))
                    .make()
                    .load(getClass().getClassLoader())
                    .getLoaded();

            return proxyType.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
            return target;
        }
    }

    /**
     * get the singleton instance (uses newInstance())
     *
     * @return singleton instance
     */
    private Object getInstance() {
        synchronized (this) {
            if (instance == null) {
                instance = newInstance();
            }
        }

        return instance;
    }

    /**
     * a simple interceptor that logs ms for invocation
     */
    public static class Interceptor {

        private final Object target;

        private Interceptor(Object target) {
            this.target = target;
        }

         @RuntimeType
         public Object intercept(@Origin Method method, @AllArguments Object[] arguments) throws Exception {
            long start = System.currentTimeMillis();
            try {
                return method.invoke(target, arguments);
            } finally {
                log.info("invocation of " + method.getName() + " in " + (System.currentTimeMillis() - start) + " ms");
            }
        }
    }
}
