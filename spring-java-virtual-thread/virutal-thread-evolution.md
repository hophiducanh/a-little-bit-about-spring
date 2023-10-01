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

