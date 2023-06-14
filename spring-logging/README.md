- https://stackify.com/compare-java-logging-frameworks/

## Java Logging
- [Tutorial](https://www.logicbig.com/tutorials/core-java-tutorial/logging.html)
- [Why not use java.util.logging?](https://stackoverflow.com/questions/11359187/why-not-use-java-util-logging/11360517#11360517)

There are a few reasons why you might not want to use java.util.logging (JUL).

* **JUL is not as flexible as other logging frameworks.** JUL does not support as many features as other logging frameworks, such as Logback and Log4J. For example, JUL does not support asynchronous logging, which can improve performance.
* **JUL is not as well-maintained as other logging frameworks.** JUL has not been updated as frequently as other logging frameworks, such as Logback and Log4J. This means that JUL may not be as secure or reliable as other logging frameworks.
* **JUL is not as widely used as other logging frameworks.** JUL is not as widely used as other logging frameworks, such as Logback and Log4J. This means that there may be less support available for JUL, such as tutorials, documentation, and troubleshooting resources. (`java.util.logging` was introduced in Java 1.4. There were uses for logging before that. That's why many other logging APIs exist. Those APIs were used heavily before Java 1.4 and thus had a great market share that didn't just drop to zero when 1.4 was release.)



If you are looking for a flexible, well-maintained, and widely used logging framework, then you should consider using Logback or Log4J instead of JUL.

Here are some of the advantages of using Logback or Log4J over JUL:

* **Flexibility:** Logback and Log4J are both very flexible logging frameworks. They support a wide range of features, such as asynchronous logging, custom layouts, and conditional logging.
* **Reliability:** Logback and Log4J are both very reliable logging frameworks. They have been extensively tested and are used by many large and complex applications.
* **Community support:** Logback and Log4J have large and active communities. This means that there is a lot of support available, such as tutorials, documentation, and troubleshooting resources.

If you are looking for a logging framework that is flexible, reliable, and has a large and active community, then you should consider using Logback or Log4J.

## Java Logging Evolution 
The Java logging landscape has evolved significantly over the years. In the early days of Java, there was no standard logging framework. Instead, each Java application had its own custom logging framework. This made it difficult to develop and maintain applications, as developers had to learn the nuances of each different logging framework.

In 1999, Ceki Gülcü developed Log4J, the first standard logging framework for Java. Log4J quickly became the de facto standard for Java logging, and it is still widely used today.

In 2004, Ceki Gülcü also developed SLF4J, a facade for logging frameworks. SLF4J allows developers to use a single API to access different logging frameworks, such as Log4J, Logback, and JUL.

In 2011, Ceki Gülcü developed Logback, a successor to Log4J. Logback is designed to be more efficient and easier to use than Log4J.

Today, there are a number of different logging frameworks available for Java. However, SLF4J is the most popular choice, as it provides a single API that can be used to access different logging frameworks.

The following table summarizes the evolution of Java logging:

| Year | Event |
|---|---|
| 1999 | Log4J is released. |
| 2004 | SLF4J is released. |
| 2011 | Logback is released. |

The following are some of the benefits of using SLF4J:

* **Portability:** SLF4J is a facade for logging frameworks, which means that developers can use a single API to access different logging frameworks. This makes it easier to port applications to different platforms.
* **Flexibility:** SLF4J allows developers to choose the logging framework that best meets their needs.
* **Performance:** SLF4J is designed to be efficient, which can improve the performance of applications.

If you are developing a Java application, I recommend using SLF4J for logging. SLF4J provides a number of benefits, including portability, flexibility, and performance.

## Performance
- https://logging.apache.org/log4j/2.x/performance.html