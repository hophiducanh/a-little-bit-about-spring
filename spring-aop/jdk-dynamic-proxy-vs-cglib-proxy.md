## Cglib proxy vs JDK dynamic proxy difference in java

**JDK dynamic proxy**

* Built into the JDK
* Can only proxy objects that implement interfaces
* Creates a new proxy object that implements the same interfaces as the target object
* Uses reflection to invoke methods on the target object

**CGLIB proxy**

* Third-party library
* Can proxy objects that implement interfaces or do not implement interfaces
* Creates a new proxy object that extends the target object
* Uses bytecode manipulation to intercept and invoke methods on the target object

**Differences**

| Feature | JDK dynamic proxy | CGLIB proxy |
|---|---|---|
| Can proxy objects that implement interfaces | Yes | Yes |
| Can proxy objects that do not implement interfaces | No | Yes |
| How the proxy object is created | Implements the same interfaces as the target object | Extends the target object |
| How method calls are intercepted | Uses reflection | Uses bytecode manipulation |
| Performance | Slightly slower | Slightly faster |

**When to use each type of proxy**

* **JDK dynamic proxy:** Use this type of proxy if the target object implements interfaces. This is the simplest and most common type of proxy to use.
* **CGLIB proxy:** Use this type of proxy if the target object does not implement interfaces or if you need to intercept final methods.

**Spring AOP**

Spring AOP uses both JDK dynamic proxies and CGLIB proxies. If the target object implements interfaces, Spring will use a JDK dynamic proxy. If the target object does not implement interfaces, Spring will use a CGLIB proxy.

**Conclusion**

Both JDK dynamic proxies and CGLIB proxies can be used to create proxies for objects in Java. The best type of proxy to use depends on the specific needs of your application.