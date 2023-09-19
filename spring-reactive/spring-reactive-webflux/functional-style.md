## Functional style

Why this is call functional style:

```shell
@Bean
public RouterFunction<ServerResponse> routerFunction() {
    return RouterFunctions
            .route()
            .GET("/router/customers", customerHandler::loadCustomers)
            .GET("/router/customers/stream", customerStreamHandler::loadCustomersStream)
            .GET("/router/customers/{input}", customerHandler::findCustomer)
            .POST("/router/customers", customerHandler::saveCustomer)
            .build();
}
```

The code you've provided is written in a functional style because it uses a functional programming approach to define and configure a routing mechanism. This is commonly seen in frameworks like Spring WebFlux, which allows you to create reactive, non-blocking web applications.

Here's why it's considered a functional style:

1. **Declarative Configuration:** In functional programming, you declare what you want to achieve rather than giving step-by-step instructions on how to achieve it. In this code, you're declaring the routing rules using a fluent API without explicitly specifying the sequence of operations required to set up the routing.

2. **Immutable Data:** Functional programming often emphasizes immutability, and this code defines a RouterFunction<ServerResponse> object, which once created, cannot be modified. You're building a routing configuration by chaining method calls, and each call returns a new immutable RouterFunction object.

3. **Lambda Expressions:** You're using lambda expressions (`customerHandler::loadCustomers`, `customerStreamHandler::loadCustomersStream`, etc.) to define the behavior associated with each route. Lambdas are a fundamental feature of functional programming, allowing you to pass behavior as data.

4. **Function Composition:** The `RouterFunctions.route()` method returns a `RouterFunctions.Builder` object, which allows you to compose multiple route definitions using method chaining. This is a common pattern in functional programming, where you build complex functionality by composing simpler functions.

5. **No Side Effects:** Functional programming encourages avoiding side effects and mutable state. In this code, you're not modifying any shared state or variables outside the method; you're simply defining a routing configuration.

Overall, the code snippet you provided embraces functional programming principles by focusing on immutability, declarative configuration, and the use of lambda expressions for behavior definition. This makes it a functional style of defining routes in a web application.