- [Curated list](#markdown-navigation)
    - [Spring into the future: Embracing virtual threads with Java's Project Loom](#spring-into-the-future-embracing-virtual-threads-with-javas-project-loom)
    - [Beyond Million Threads - All you need to know about Virtual Threads](#beyond-million-threads—all-you-need-to-know-about-virtual-threads)

## <a id="#spring-into-the-future-embracing-virtual-threads-with-javas-project-loom"></a>[Spring into the future: Embracing virtual threads with Java's Project Loom](https://www.danvega.dev/blog/2023/04/12/virtual-threads-spring/)

### WHAT IS PROJECT LOOM?
Project Loom aims to reduce the effort of writing, maintaining, and observing high-throughput concurrent applications. It introduces virtual threads as a solution to improve performance and scalability, particularly when dealing with blocking APIs commonly found in Spring MVC applications.

It's been announced that virtual threads are targeted for JDK 21, which is exciting news as it means they might show up in Spring Framework 6.1 (also targeted for later this year). So it's about time we all learn about virtual threads and why we might care about them as Spring developers.

### HISTORY: WHY WE NEED VIRTUAL THREADS
Let's first understand the thread-per-request model and how threads work in Java. When a client makes a request to a server, the request often involves the server to process blocking operations, such as reading or persisting from a database using JDBC or JPA, writing to a file, or communicating with another service.

This application works great on your laptop and during the initial stages of implementation. However, once news spreads about your exceptional new application, you may start experiencing a high volume of traffic that could cause it to crash.

Why is this happening? What can you do to address this?

![image](https://user-images.githubusercontent.com/22516811/271819528-c581ea8e-cb21-4397-b3ba-8101084cdc68.png)

To answer these questions you must understand the thread-per-request model. When a request is handled by the web server it is using a Java Thread and that thread is tied to an operating system thread. When you make a call to a blocking operation like reading or persisting to a database that thread is blocked from doing anything else until the request is fulfilled.

The thread-per-request model ties up threads in the system, and there is a maximum number of concurrent threads allowed. When the number of maximum threads has been reached, each subsequent request will need to wait for a thread to be released to fulfill that request. This can cause slowness or even errors in the application when it experiences high traffic.

![image](https://user-images.githubusercontent.com/22516811/271819583-ce0337e7-d3c3-4909-a43e-8f70dd16bf1b.png)

![image](https://user-images.githubusercontent.com/22516811/271819599-11c7229d-5e04-415a-8281-69b491ae510a.png)

### SCALABILITY SOLUTIONS

To improve scalability, there are two main approaches that you can currently take advantage of:
1. **Scaling Hardware**: Add more memory, CPU or servers (vertical and horizontal scaling)
2. **Asynchronous Programming**: Writing non-blocking software to optimize thread usage

![image](https://user-images.githubusercontent.com/22516811/271820009-74df9107-789d-4789-b08b-59c3abe74e88.png)

### INTRODUCTION TO VIRTUAL THREADS

Virtual threads, available as a preview release in JDK 19 and 20, are lightweight, inexpensive, and easy to create. They are tied to a platform thread that is connected to the operating system thread. Consequently, we no longer tie up platform threads in our applications and can handle more concurrent requests. The most exciting aspect of virtual threads is that we can use them in our Spring applications with little or no code changes.

![image](https://user-images.githubusercontent.com/22516811/271820031-f5af0de7-a453-4311-97c0-b40cbb56bdf3.png)

----

## <a id="#beyond-million-threads—all-you-need-to-know-about-virtual-threads"></a>[Beyond Million Threads - All you need to know about Virtual Threads](https://sankarge.medium.com/beyond-million-threads-how-java-can-retain-its-supremacy-with-virtual-threads-76cf270e8922)

![image](https://user-images.githubusercontent.com/22516811/271821878-ab11981f-e392-4116-be24-bcb64bed0d15.png)

![image](https://user-images.githubusercontent.com/22516811/271821917-984642ad-e6ba-4669-9677-aac10c09bba7.png)

----

## [Understanding Java Virtual Threads - The Death Of Async Programming](https://theboreddev.com/understanding-java-virtual-threads/)

### The Problem With Platform Threads

#### I. Parity Between OS threads and Platform Threads

Currently, in the JDK, there is a **one-to-one** relationship between Java threads (also called **platform threads**) and OS threads.

This means that when a thread is waiting for the completion of an IO operation, the underlying OS thread will remain blocked and therefore, unused, until this operation completes. This has always been a big problem in terms of scalability in the Java ecosystem, because our application is limited by the available threads in the host.

In the last decade, we have tried to address this problem with the use of **asynchronous processing libraries** and the use of futures. For example, using **CompletableFuture** we can achieve a non-blocking approach, although the readability of these models is, in many cases, not what we would expect.

#### II. The Problem with Async Programming

Although async programming is a viable solution for the threads' limitation, **writing asynchronous code is more complex than sequential**. The developer has to define callbacks to apply operations based on the response to a given task, this makes difficult to follow and reason about the code.

Another big issue is that **debugging these applications becomes hard**. A given request can be handled by multiple threads, therefore debugging, logging or analysing stack traces gets really difficult.

In terms of flexibility and maintainability, asynchronous programming is very limited too. We have to renounce to certain sequential workflow structures like loops, this means that a piece of code that has been written as sequential cannot be transformed to asynchronous easily. Exactly the same happens in the opposite case.

Lastly, but not less important, writing explicit asynchronous code is more error-prone due to the complexities that come with it.

#### III. Expensive Creation of Threads

Another problem with platform threads is that they are heavy objects which are expensive to create, therefore we need to create them beforehand and store them in a pool of threads to avoid creating new threads every time that we need a thread to run our code. Why are they expensive?

The creation of Java threads is expensive because it involves allocating memory for the thread, initialising the thread stack and also making OS calls to register the OS thread.

When we consider both problems, the limit in OS threads and how expensive it is to create platform threads, this means that we need **bounded pools of threads** to be able to run our applications safely. If we don’t use a bounded pool of threads, we are risking running out of resources with dramatic consequences for our system.

#### IV. Expensive Context Switching

The other problem of this design is how expensive context switching is. When there is a context switch, an OS thread switches from one platform thread to another, the OS has to save the local data and memory pointers for the current thread and load the ones for the new platform thread. **Context switching is a very expensive operation as it involves many CPU cycles**. The OS takes care of pausing a thread, saving its stack and allocating the new thread, this process is costly as it requires loading and unloading the thread stack.


## Does java 21 virtual threads stack store in heap memory?

Yes, Java 21 virtual threads stack store in heap memory. This is one of the key features that makes virtual threads so lightweight and efficient.

Traditional Java threads have their stack frames allocated in native memory, which is managed by the operating system. This means that each thread has a fixed stack size, which can lead to resource contention and performance problems when there are many threads running.

Virtual threads, on the other hand, store their stack frames in heap memory, which is managed by the Java garbage collector. This means that the stack size of a virtual thread can grow and shrink as needed, and there is no need to preallocate stack space for each thread.

As a result, virtual threads can be created and destroyed much more cheaply than traditional threads. This makes them ideal for applications that need to handle a large number of concurrent requests, such as web servers and databases.

In Java 21, virtual threads are implemented as a new type of thread called a **virtual thread carrier**. Each virtual thread is associated with a single virtual thread carrier, which is a traditional Java thread that manages the execution of the virtual thread.

When a virtual thread is scheduled to run, the JVM copies the stack frame of the virtual thread from the heap to the stack of the virtual thread carrier. This allows the JVM to use the same stack for multiple virtual threads, which further improves efficiency.

The JVM also uses a technique called **stack paging** to manage the stack frames of inactive virtual threads. When a virtual thread is inactive, the JVM can page out its stack frame from the heap to disk. This frees up heap memory for other purposes, and it also reduces the memory footprint of the JVM.

Overall, the use of heap memory for virtual thread stacks is a key feature that makes virtual threads so lightweight and efficient. It allows the JVM to create and manage a large number of virtual threads with minimal overhead.

## How does JDK schedules virtual threads?

The JDK schedules virtual threads using a dedicated ForkJoinPool in first-in-first-out (FIFO) mode. This pool is created and maintained by the JDK's virtual thread scheduler, and it is used to schedule the execution of virtual threads on platform threads.

When a virtual thread is created, it is added to the virtual thread scheduler's queue. The scheduler then selects a platform thread from its pool and mounts the virtual thread on the platform thread. The operating system then schedules the platform thread as usual.

When a virtual thread reaches a blocking operation, it is unmounted from its carrier thread and added back to the virtual thread scheduler's queue. The scheduler then selects another platform thread to mount the virtual thread on.

This process continues until the virtual thread terminates.

The parallelism of the JDK's virtual thread scheduler is the number of platform threads available for the purpose of scheduling virtual threads. By default, the number of platform threads equals the number of available processor cores. However, this behavior can be tuned with a system property.

Here is a simplified overview of the JDK's virtual thread scheduling algorithm:

1. When a virtual thread is created, it is added to the virtual thread scheduler's queue.
2. The scheduler selects a platform thread from its pool and mounts the virtual thread on the platform thread.
3. The operating system schedules the platform thread as usual.
4. When the virtual thread reaches a blocking operation, it is unmounted from its carrier thread and added back to the virtual thread scheduler's queue.
5. The scheduler selects another platform thread to mount the virtual thread on.
6. Repeat steps 3-5 until the virtual thread terminates.

Virtual threads are a new feature in Java 19, and they are still under development. However, they have the potential to improve the performance and scalability of Java applications.