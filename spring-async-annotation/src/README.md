- https://springhow.com/spring-async/

- [Completable Future](https://howtodoinjava.com/spring-boot2/rest/enableasync-async-controller/)
- [CompletableFuture : A new era of asynchronous programming](https://levelup.gitconnected.com/completablefuture-a-new-era-of-asynchronous-programming-86c2fe23e246)
- [Everything You Need To Know About The CompletableFuture API ](https://levelup.gitconnected.com/everything-you-need-to-know-about-the-completablefuture-api-ec357e731a5c)

## Async

> How Does **@Async** Work?
When you put an Async annotation on a method underlying it, it creates a proxy of that object where **Async** is defined **(JDK Proxy/CGlib)** based on the **proxyTargetClass** property. Then, **Spring** tries to find a thread pool associated with the context to submit this method's logic as a separate path of execution. To be exact, it searches a unique **TaskExecutor** bean or a bean named as **taskExecutor**. **If it is not found, then use the default SimpleAsyncTaskExecutor**.
>
> Now, as it creates a proxy and submits the job to the **TaskExecutor** thread pool, [it has a few limitations that have to know](https://dzone.com/articles/effective-advice-on-spring-async-part-1#:~:text=When%20you%20put%20an%20Async,a%20separate%20path%20of%20execution.). Otherwise, you will scratch your head as to why your Async did not work or create a new thread! Let's take a look.


### Stacktrace (debug in @Async method)

```shell
asyncProcessSomethingForLong:27, HelloService (com.logbasex.async_annotation.service)
invoke:-1, HelloService$$FastClassBySpringCGLIB$$5a1f6d3a (com.logbasex.async_annotation.service)
invoke:218, MethodProxy (org.springframework.cglib.proxy)
invokeJoinpoint:793, CglibAopProxy$CglibMethodInvocation (org.springframework.aop.framework)
proceed:163, ReflectiveMethodInvocation (org.springframework.aop.framework)
proceed:763, CglibAopProxy$CglibMethodInvocation (org.springframework.aop.framework)
lambda$invoke$0:115, AsyncExecutionInterceptor (org.springframework.aop.interceptor)
call:-1, 2123867258 (org.springframework.aop.interceptor.AsyncExecutionInterceptor$$Lambda$597)
run$$$capture:266, FutureTask (java.util.concurrent)
run:-1, FutureTask (java.util.concurrent)

 - Async stack trace
 
<init>:132, FutureTask (java.util.concurrent)
newTaskFor:102, AbstractExecutorService (java.util.concurrent)
submit:133, AbstractExecutorService (java.util.concurrent)
submit:388, ThreadPoolTaskExecutor (org.springframework.scheduling.concurrent)
doSubmit:292, AsyncExecutionAspectSupport (org.springframework.aop.interceptor)
invoke:129, AsyncExecutionInterceptor (org.springframework.aop.interceptor)
proceed:186, ReflectiveMethodInvocation (org.springframework.aop.framework)
proceed:763, CglibAopProxy$CglibMethodInvocation (org.springframework.aop.framework)
intercept:708, CglibAopProxy$DynamicAdvisedInterceptor (org.springframework.aop.framework)
asyncProcessSomethingForLong:-1, HelloService$$EnhancerBySpringCGLIB$$30317422 (com.logbasex.async_annotation.service)
helloAsync:27, HelloWorldController (com.logbasex.async_annotation.controller)
invoke0:-2, NativeMethodAccessorImpl (sun.reflect)
invoke:62, NativeMethodAccessorImpl (sun.reflect)
invoke:43, DelegatingMethodAccessorImpl (sun.reflect)
invoke:498, Method (java.lang.reflect)
doInvoke:205, InvocableHandlerMethod (org.springframework.web.method.support)
invokeForRequest:150, InvocableHandlerMethod (org.springframework.web.method.support)
invokeAndHandle:117, ServletInvocableHandlerMethod (org.springframework.web.servlet.mvc.method.annotation)
invokeHandlerMethod:895, RequestMappingHandlerAdapter (org.springframework.web.servlet.mvc.method.annotation)
handleInternal:808, RequestMappingHandlerAdapter (org.springframework.web.servlet.mvc.method.annotation)
handle:87, AbstractHandlerMethodAdapter (org.springframework.web.servlet.mvc.method)
doDispatch:1070, DispatcherServlet (org.springframework.web.servlet)
doService:963, DispatcherServlet (org.springframework.web.servlet)
processRequest:1006, FrameworkServlet (org.springframework.web.servlet)
doGet:898, FrameworkServlet (org.springframework.web.servlet)
service:655, HttpServlet (javax.servlet.http)
service:883, FrameworkServlet (org.springframework.web.servlet)
service:764, HttpServlet (javax.servlet.http)
internalDoFilter:227, ApplicationFilterChain (org.apache.catalina.core)
doFilter:162, ApplicationFilterChain (org.apache.catalina.core)
doFilter:53, WsFilter (org.apache.tomcat.websocket.server)
internalDoFilter:189, ApplicationFilterChain (org.apache.catalina.core)
doFilter:162, ApplicationFilterChain (org.apache.catalina.core)
doFilterInternal:100, RequestContextFilter (org.springframework.web.filter)
doFilter:117, OncePerRequestFilter (org.springframework.web.filter)
internalDoFilter:189, ApplicationFilterChain (org.apache.catalina.core)
doFilter:162, ApplicationFilterChain (org.apache.catalina.core)
doFilterInternal:93, FormContentFilter (org.springframework.web.filter)
doFilter:117, OncePerRequestFilter (org.springframework.web.filter)
internalDoFilter:189, ApplicationFilterChain (org.apache.catalina.core)
doFilter:162, ApplicationFilterChain (org.apache.catalina.core)
doFilterInternal:201, CharacterEncodingFilter (org.springframework.web.filter)
doFilter:117, OncePerRequestFilter (org.springframework.web.filter)
internalDoFilter:189, ApplicationFilterChain (org.apache.catalina.core)
doFilter:162, ApplicationFilterChain (org.apache.catalina.core)
invoke:197, StandardWrapperValve (org.apache.catalina.core)
invoke:97, StandardContextValve (org.apache.catalina.core)
invoke:541, AuthenticatorBase (org.apache.catalina.authenticator)
invoke:135, StandardHostValve (org.apache.catalina.core)
invoke:92, ErrorReportValve (org.apache.catalina.valves)
invoke:78, StandardEngineValve (org.apache.catalina.core)
service:360, CoyoteAdapter (org.apache.catalina.connector)
service:399, Http11Processor (org.apache.coyote.http11)
process:65, AbstractProcessorLight (org.apache.coyote)
process:890, AbstractProtocol$ConnectionHandler (org.apache.coyote)
doRun:1789, NioEndpoint$SocketProcessor (org.apache.tomcat.util.net)
run:49, SocketProcessorBase (org.apache.tomcat.util.net)
runWorker:1191, ThreadPoolExecutor (org.apache.tomcat.util.threads)
run:659, ThreadPoolExecutor$Worker (org.apache.tomcat.util.threads)
run:61, TaskThread$WrappingRunnable (org.apache.tomcat.util.threads)
run:750, Thread (java.lang)
```

## Spring @Async vs CompletableFuture.supplyAsync()

- [is using @Async and CompletableFuture in controller can increase performance of our api?](https://stackoverflow.com/questions/65120202/is-using-async-and-completablefuture-in-controller-can-increase-performance-of)

### Does @Async as same as completableFuture.supplyAsync() if i want to execute task async only?

Yes, when you want to execute a task asynchronously without dealing with the complexities of managing threads directly, both `@Async` and `CompletableFuture.supplyAsync()` can achieve similar results. They abstract away the thread management details and allow you to focus on writing asynchronous code.

Here's a comparison between `@Async` and `CompletableFuture.supplyAsync()` for executing a task asynchronously:

1. **@Async:**
    - Spring's `@Async` annotation is a higher-level approach to asynchronous execution within a Spring application.
    - It requires configuration of the `@EnableAsync` annotation and an appropriate thread pool setup.
    - By annotating a method with `@Async`, Spring handles the thread creation and management for you.

   ```java
   @Async
   public CompletableFuture<String> asyncMethod() {
       // Asynchronous logic
       return CompletableFuture.completedFuture("Async task completed");
   }
   ```

2. **CompletableFuture.supplyAsync():**
    - `CompletableFuture.supplyAsync()` is a lower-level approach available in the Java standard library for creating asynchronous tasks.
    - You directly specify the logic to be executed asynchronously and can optionally provide an `Executor` to control the thread pool used for execution.

   ```java
   CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
       // Asynchronous logic
       return "Async task completed";
   });
   ```

In both cases, the main idea is to execute code asynchronously without blocking the main thread. If you are working within a Spring application and have already configured the necessary `@EnableAsync` setup, using `@Async` can be more concise and integrated.

If you are working outside of a Spring context or want more control over the thread pool, using `CompletableFuture.supplyAsync()` gives you greater flexibility.

Ultimately, whether you choose `@Async` or `CompletableFuture.supplyAsync()` depends on your specific use case and the level of integration you want with the Spring framework.

---

### Does combine @Async and  completableFuture.supplyAsync() improve performance if I want to execute task async only?

Combining `@Async` and `CompletableFuture.supplyAsync()` would not provide any additional benefits in terms of performance if your goal is to execute a task asynchronously. These two approaches essentially achieve the same result, but using them together would introduce unnecessary complexity without adding value.

You should choose one of these approaches based on your application's requirements and the level of integration you need:

1. **Using `@Async`**:
    - If you're already working within a Spring application and have configured `@EnableAsync`, it's more straightforward to use `@Async` for executing tasks asynchronously.
    - Spring's `@Async` abstracts away the thread management and integrates well within the Spring framework.
    - It's a higher-level approach that simplifies the process of executing methods asynchronously.

2. **Using `CompletableFuture.supplyAsync()`**:
    - If you're not using Spring or want to maintain more control over the thread pool, you can use `CompletableFuture.supplyAsync()`.
    - This approach is available in the Java standard library and can be used in any Java application.
    - It provides more flexibility in terms of specifying the executor for thread management.

Using both approaches together wouldn't provide any performance improvement and might even complicate your code unnecessarily. Choose the approach that best fits your application's context and requirements. If your primary goal is to execute a task asynchronously, either `@Async` or `CompletableFuture.supplyAsync()` would be sufficient on its own.

---

### How does Spring get the result from an endpoint that returns CompletableFuture object?

In a Spring application, when you return a `CompletableFuture` from a controller method, Spring's web framework (e.g., Spring MVC or Spring WebFlux) is responsible for managing the asynchronous processing and handling of the result. Here's how Spring gets the result from an endpoint that returns a `CompletableFuture`:

1. **Controller Invocation**: When a client makes a request to an endpoint that returns a `CompletableFuture`, Spring's dispatcher servlet receives the request and identifies the appropriate controller method to handle it based on the URL mapping.

2. **Controller Method Execution**: The controller method is invoked, and it returns the `CompletableFuture` immediately. At this point, the controller method is free to continue executing other tasks, and it doesn't block the main thread.

3. **CompletableFuture Processing**: Spring's web framework registers a callback to be executed when the `CompletableFuture` is completed. This callback typically involves using Java's `CompletionStage` API to register a `thenAccept` or `thenApply` function.

4. **Asynchronous Processing**: The asynchronous processing within the `CompletableFuture` continues in a separate thread or executor pool. This can involve performing time-consuming operations, waiting for external services, or other asynchronous tasks.

5. **Completion of CompletableFuture**: When the `CompletableFuture` computation is finished (either successfully or exceptionally), the registered callback is executed.

6. **Handling the Result**: Depending on the type of callback registered, Spring will handle the result in one of two ways:

   - If the `CompletableFuture` is returning a value, Spring will use the result to construct the HTTP response. For example, if the `CompletableFuture` contains a `String`, Spring will convert it to a JSON or plain text response and send it to the client.

   - If the `CompletableFuture` completes exceptionally with an error, Spring will handle the error by returning an appropriate HTTP error response (e.g., a 500 Internal Server Error) and possibly logging the error.

7. **Response to the Client**: The result of the `CompletableFuture` is sent as an HTTP response to the client, and the client receives the response.

By using `CompletableFuture`, Spring can efficiently handle a large number of concurrent requests without blocking the server's main thread. It leverages the non-blocking, asynchronous nature of `CompletableFuture` to keep the application responsive.

Here's an example of a Spring controller method that returns a `CompletableFuture`:

```java
@RestController
public class MyController {

    @Autowired
    private MyService myService;

    @GetMapping("/async")
    public CompletableFuture<String> asyncMethod() {
        return myService.performAsyncTask();
    }
}
```

In this example, `myService.performAsyncTask()` returns a `CompletableFuture<String>`, and Spring takes care of the asynchronous processing and returning the result to the client when the `CompletableFuture` completes.

----

## [Is using @Async and CompletableFuture in controller can increase performance of our API?](https://stackoverflow.com/questions/65120202/is-using-async-and-completablefuture-in-controller-can-increase-performance-of/65185737#65185737)

---

## [8+ things you need to know when you want to use Spring @Async really well](https://levelup.gitconnected.com/8-things-you-need-to-know-when-you-want-to-use-spring-async-really-well-e5af4af259c5)


To use asynchronous programming effectively, it's crucial to understand when and why to use it, and to be aware of potential pitfalls. Here are some guidelines for using asynchronous programming effectively:

- **EnableAsync Annotation:** In a Spring Boot application, add `@EnableAsync` to the startup class to enable asynchronous processing.

- **Accessibility:** Annotated methods should not be static or private; they must be public.

- **Component Annotation:** Ensure the class containing the asynchronous method is annotated with `@Component` or other appropriate annotations to allow Spring to scan for asynchronous classes.

- **Avoid Self-Invocation:** Avoid invoking an asynchronous method from within the same class, as it bypasses the proxy and loses the asynchronous effect.

- **Transactional Operations:** Avoid annotating `@Transactional` on asynchronous methods directly. Instead, place `@Transactional` on methods called internally.

- **Avoid Circular Dependencies:** Be cautious when using asynchronous methods in classes with circular dependencies, as it can lead to circular dependency exceptions.

- **Handling Return Values:** When dealing with asynchronous methods returning `Future`, use appropriate techniques to wait for and handle the results.

- **Custom Thread Pools:** Customize thread pool configurations for different asynchronous tasks to optimize resource utilization.

- **Exception Handling:** Implement a custom `AsyncUncaughtExceptionHandler` to handle exceptions that occur within asynchronous methods.

- **Transaction Handling:** Ensure proper transaction handling in asynchronous methods by carefully structuring your code.