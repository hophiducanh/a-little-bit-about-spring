## why completableFuture come into exists

CompletableFuture came into existence because of the need for a more powerful and flexible way to do asynchronous programming in Java. Before CompletableFuture, there were a few different ways to do asynchronous programming in Java, but they all had limitations.

* **Callbacks:** Callbacks are a way to pass a function to another function, and then have the first function call the second function when it finishes. Callbacks are simple to use, but they can be difficult to manage when you have multiple callbacks nested inside of each other.
* **Futures:** Futures are a more powerful way to do asynchronous programming than callbacks. Futures allow you to get the result of an asynchronous computation, even if the computation hasn't finished yet. Futures can also be chained together, so you can do things like wait for the result of one computation before starting another computation.
* **ExecutorService:** ExecutorService is a class that provides a way to run tasks in parallel. ExecutorService can be used to do asynchronous programming, but it's not as easy to use as CompletableFuture.

CompletableFuture combines the power of futures with the flexibility of callbacks. It's easy to use, and it provides a lot of features that make asynchronous programming in Java easier.

Here are some of the benefits of using CompletableFuture:

* **Easy to use:** CompletableFuture is easy to learn and use. The API is designed to be intuitive, and there are plenty of examples available online.
* **Flexible:** CompletableFuture is very flexible. You can use it to do a variety of asynchronous tasks, such as making network requests, reading and writing files, and running computations in parallel.
* **Powerful:** CompletableFuture is a powerful tool. It provides a lot of features that make asynchronous programming in Java easier, such as chaining futures together, handling exceptions, and attaching callbacks.

Overall, CompletableFuture is a great tool for asynchronous programming in Java. It's easy to use, flexible, and powerful.

-------------

