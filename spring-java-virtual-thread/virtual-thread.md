## [Virtual Threads in Java](https://www.happycoders.eu/java/virtual-threads/)

### Why Do We Need Virtual Threads?

Anyone who has ever maintained a backend application under heavy load knows that threads are often the bottleneck. For every incoming request, a thread is needed to process the request. One Java thread corresponds to one operating system thread, and those are resource-hungry:

- An OS thread reserves 1 MB for the stack and commits 32 or 64 KB of it upfront, depending on the operating system.
- It takes about 1ms to start an OS thread.
- Context switches take place in kernel space and are quite CPU-intensive.

You should not start more than a few thousand; otherwise, you risk the stability of the entire system.

However, a few thousand are not always enough – especially if it takes longer to process a request because of the need to wait for blocking data structures, such as queues, locks, or external services like databases, microservices, or cloud APIs.

For example, if a request takes two seconds and we limit the thread pool to 1,000 threads, then a maximum of 500 requests per second could be answered. However, the CPU would be far from being utilized since it would spend most of its time waiting for responses from the external services, even if several threads are served per CPU core.

So far, we have only been able to overcome this problem with asynchronous programming – for example, with CompletableFuture or reactive frameworks like RxJava and Project Reactor.

However, anyone who has had to maintain code like the following knows that reactive code is many times more complex than sequential code – and absolutely no fun.

```jshelllanguage
public CompletionStage<Response> getProduct(String productId) {
  return productService
      .getProductAsync(productId)
      .thenCompose(
          product -> {
            if (product.isEmpty()) {
              return CompletableFuture.completedFuture(
                  Response.status(Status.NOT_FOUND).build());
            }

            return warehouseService
                .isAvailableAsync(productId)
                .thenCompose(
                    available ->
                        available
                            ? CompletableFuture.completedFuture(0)
                            : supplierService.getDeliveryTimeAsync(
                                product.get().supplier(), productId))
                .thenApply(
                    daysUntilShippable ->
                        Response.ok(
                                new ProductPageResponse(
                                    product.get(), daysUntilShippable))
                            .build());
          });
}
```

Not only is this code hard to read and maintain, but it is also extremely difficult to debug. For example, it would make no sense to set a breakpoint here because the code only defines the asynchronous flow but does not execute it. The business code will be executed in a separate thread pool at a later time.

In addition, the database drivers and drivers for other external services must also support the asynchronous, non-blocking model.

### Advantages of Virtual Threads
Virtual threads offer impressive advantages:

First, they are inexpensive:

- They can be created much faster than platform threads: it takes about 1 ms to create a platform thread, and less than 1 µs to create a virtual thread.
- They require less memory: a platform thread reserves 1 MB for the stack and commits 32 to 64 KB up front, depending on the operating system. A virtual thread starts with about one KB. However, this is true only for flat call stacks. A call stack the size of a half megabyte requires that half megabyte in both thread variants.
- Blocking virtual threads is cheap because a blocked virtual thread does not block an OS thread. However, it's not free as its stack needs to be copied to the heap.
- Context switches are fast because they are performed in user space, not kernel space, and numerous optimizations have been made in the JVM to make them faster.

Second, we can use virtual threads in a familiar way:

- Only minimal changes have been made to the Thread and ExecutorService APIs.
- Instead of writing asynchronous code with callbacks, we can write code in the traditional blocking thread-per-request style.
- We can debug, observe, and profile virtual threads with existing tools.

### What Are Virtual Threads Not?

Virtual threads don't have only advantages, of course. Let's first look at what virtual threads are not, and what we cannot or should not do with them:

- Virtual threads are not faster threads – they cannot execute more CPU instructions than a platform thread in the same amount of time.
- They are not preemptive: while a virtual thread is executing a CPU-intensive task, it is not unmounted from the carrier thread. So if you have 20 carrier threads and 20 virtual threads that occupy the CPU without blocking, no other virtual thread will be executed.
- They do not provide a higher level of abstraction than platform threads. You need to be aware of all the subtle things that you also need to be aware of when using regular threads. That is, if a virtual thread accesses shared data, you have to take care of visibility issues, you have to synchronize atomic operations, and so on.

## [Beyond Million Threads — All you need to know about Virtual Threads](https://sankarge.medium.com/beyond-million-threads-how-java-can-retain-its-supremacy-with-virtual-threads-76cf270e8922)

> The Virtual Threads are smart. They know when to hold onto an OS Thread. Most importantly, when to let go of.

## [An Era of Virtual Threads: Java](https://medium.com/@pradeesh-kumar/an-era-of-virtual-threads-java-b839a844efee)