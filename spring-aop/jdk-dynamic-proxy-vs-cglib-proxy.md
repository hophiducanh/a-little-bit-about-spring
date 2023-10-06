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
 
## Why spring boot switch from jdk dynamic proxy to cglib proxy?

Spring Boot switched from using JDK dynamic proxies to CGLIB proxies for several reasons:

1. **Performance**: CGLIB proxies generally offer better performance than JDK dynamic proxies. This is because CGLIB generates bytecode at runtime to create proxy classes, which can be optimized for specific use cases. In contrast, JDK dynamic proxies rely on reflection, which can be slower.

2. **Constructor-based Proxies**: CGLIB allows proxying classes with constructors, while JDK dynamic proxies can only proxy interfaces. This limitation of JDK dynamic proxies can be problematic when you need to create proxies for classes that don't implement interfaces.

3. **More Flexibility**: CGLIB proxies provide more flexibility in proxying classes and methods. You can create proxies for classes without having to create interfaces, and you can proxy non-public methods.

4. **Improved AOP Support**: Spring's Aspect-Oriented Programming (AOP) framework benefits from the enhanced capabilities of CGLIB. CGLIB proxies can be used to create proxies for classes, enabling AOP on non-interface methods and classes without needing to implement interfaces.

5. **Compatibility**: While JDK dynamic proxies have some advantages, such as not requiring a third-party library like CGLIB, they have limitations. By switching to CGLIB, Spring Boot could offer a more comprehensive solution for proxying, which aligns with the needs of many Spring applications.

In summary, Spring Boot switched to CGLIB proxies primarily for performance improvements, increased flexibility, and better support for AOP. This change allowed Spring Boot to provide a more robust and versatile solution for creating proxies in Spring applications.
