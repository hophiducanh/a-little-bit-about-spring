## [Pitfalls of using Virtual Threads](https://www.mastertheboss.com/java/mastering-virtual-threads-a-comprehensive-tutorial/?expand_article=1)

Although Virtual Threads are a huge addition to simplify the design and execution of reactive applications, there are a few aspects to consider before adding them blindly in your core.

- Firstly, consider that you need to use a Virtual Thread in an `asynchronous` context. On the other hand, if your Virtual Thread executes a `synchronized` method (or block), **it will retain control over the underlying OS thread**. Consequently, it will be **pinned** to its carrier, so if you perform a block operation there, you are also blocking the carrier.


- Then, consider that with a large context switch scenario, such as thousands of Threads, the virtual thread can be assigned to a different Carrier before it completes its job. That can eventually cause Thread cache misses as Thread local structures needs to be recreated each time the Carrier changes.


- Finally, consider that virtual threads are a good option for IO bound applications but **not for CPU bound applications**. The reason is that Virtual Threads are **not pre-emptive** by design. They will perform their task until it’s complete. The ideal scenario would be to wait for some incoming IO. On the other hand, if your Virtual Thread will just perform CPU intensive operations, they cannot be discontinued. Therefore, you will eventually lose fairness in your application processes.