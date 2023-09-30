# Actor model

## [Actor Model : Basics before going deep.](https://medium.com/knoldus/actor-model-basics-before-going-deep-800a4b1e12d4)

We have CPUs, and they are getting better every day, in helping us process fast. But CPUs are not getting faster!!

What’s happening is that we now have multiple cores on them. And, to take advantage of this, we need a way to run our code concurrently.

Some concurrency models that we are already familiar with, are:

- Processes
- Threads
- Futures
- Coroutines

For concurrency, we as developers have used Threads. But Decades of untraceable bugs and developers’ ache have shown that threads are not the way to go. Anyone who has done multi-threading in the past won’t deny how hard and painful it is to manage multithreading applications.

But fear not, for developers who have experienced the problems with creating and managing multithreading applications and are looking for a higher level of abstraction to get away from threads and locks, there are great alternatives out there and one of them is : The Actor Model.

#### What is Actor Model?

> Actor Model provides a level of abstraction that makes it easier to write correct concurrent, parallel and distributed systems.

## [Type-safe actor model for Java](https://medium.com/@zakgof/type-safe-actor-model-for-java-7133857a9f72)

Concurrent programming has always been tricky. Accessing a shared state from multiple threads is a central problem, where hard-to-catch errors may easily appear. Java has all the instruments for safe and flawless concurrency, but the compiler would deliberately let developers write dangerous code. There is a strong need for higher-level frameworks ensuring safe concurrent programming.

## Why actor framework come into exists?

The Actor model and Actor frameworks like Akka were developed to address the challenges of managing concurrency and building scalable, fault-tolerant distributed systems. Here are some reasons why the Actor model and frameworks like Akka came into existence:

1. **Concurrency Challenges:** Traditional approaches to managing concurrency in languages like Java using threads and locks can be error-prone and complex. Developers often face issues like race conditions, deadlocks, and shared memory conflicts. The Actor model provides a higher-level abstraction that simplifies concurrent programming.

2. **Message Passing:** The Actor model's fundamental concept is message passing, where actors communicate by sending and receiving messages. This approach promotes a clear and controlled way of sharing information between concurrent entities, reducing the likelihood of concurrency bugs.

3. **Isolation:** In the Actor model, each actor encapsulates its state and behavior, making it independent of other actors. This isolation of state helps prevent unintended interactions between concurrent entities, leading to more predictable and maintainable code.

4. **Scalability:** Actors can be created and distributed across multiple machines, making them well-suited for building scalable systems. The Actor model allows for easy parallelism and load balancing, enabling applications to take full advantage of modern multi-core processors and distributed computing environments.

5. **Fault Tolerance:** Many Actor frameworks, including Akka, provide built-in mechanisms for handling errors and failures. Actors can be designed to supervise and recover from failures, ensuring that a single error doesn't bring down the entire system.

6. **Distributed Systems:** The Actor model naturally fits the architecture of distributed systems. With the rise of distributed computing and the need for applications to scale horizontally, frameworks like Akka enable the development of distributed, fault-tolerant systems without the complexity of low-level network programming.

7. **Concurrency in the Cloud:** As cloud computing became more prevalent, the Actor model and frameworks like Akka gained popularity. These frameworks facilitate the development of cloud-native applications that can easily scale and adapt to changing workloads.

8. **Language Agnostic:** The Actor model is not tied to a specific programming language. It is a conceptual model that can be implemented in various languages, making it adaptable to different development ecosystems.

Overall, the Actor model and frameworks like Akka emerged as a response to the increasing demand for reliable, scalable, and concurrent software systems. They provide a high-level, message-passing-based abstraction that simplifies concurrent and distributed programming while addressing many of the challenges associated with traditional concurrency approaches.

## Show me examples before and after actor framework come to exist?

Certainly! Let's compare a simple example of managing concurrency in Java before and after the Actor framework (such as Akka) existed. We'll use a basic scenario of concurrent message processing to illustrate the differences.

**Before Actor Framework:**

Before the Actor framework was widely adopted, managing concurrency in Java often involved using low-level mechanisms like threads and synchronization. Here's an example of processing messages from multiple threads using traditional Java concurrency:

```java
import java.util.concurrent.*;

public class MessageProcessor {
    public static void main(String[] args) {
        int numThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                while (true) {
                    Message message = MessageQueue.getNextMessage();
                    if (message == null) {
                        break; // Terminate thread when there are no more messages
                    }
                    processMessage(message);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static class Message {
        // Message data and methods
    }

    static class MessageQueue {
        private static final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

        static Message getNextMessage() {
            try {
                return queue.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        static void enqueueMessage(Message message) {
            queue.offer(message);
        }
    }

    static void processMessage(Message message) {
        // Process the message
    }
}
```

In this example, we manually manage threads, create a message queue, and use low-level synchronization techniques like `BlockingQueue` to handle message passing and thread coordination.

**After Actor Framework (e.g., Akka):**

With the Actor framework, such as Akka, managing concurrency becomes more straightforward. Actors are used to encapsulate state and behavior, and message passing is simplified. Here's an example of the same message processing scenario using Akka:

```java
import akka.actor.*;
import akka.dispatch.*;

public class MessageProcessor {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MessageProcessorSystem");
        ActorRef messageProcessor = system.actorOf(Props.create(MessageProcessorActor.class));

        // Send messages to the actor
        messageProcessor.tell(new Message(), ActorRef.noSender());
        messageProcessor.tell(new Message(), ActorRef.noSender());

        // Terminate the actor system when done
        system.terminate();
    }

    static class MessageProcessorActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder()
                .match(Message.class, this::processMessage)
                .build();
        }

        private void processMessage(Message message) {
            // Process the message
        }
    }

    static class Message {
        // Message data and methods
    }
}
```

In this example, we define an actor using Akka, and the framework takes care of managing the actor's lifecycle, message passing, and concurrency. It simplifies the code and makes it more focused on the application logic rather than low-level concurrency details.

Overall, the Actor framework (like Akka) provides a higher-level abstraction for managing concurrency and message passing, making it easier to develop concurrent and distributed systems.
