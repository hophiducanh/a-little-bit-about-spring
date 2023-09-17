## Analogy

- [Reactive programming and Project Reactor](https://medium.com/ing-tech-romania/spring-reactive-from-zero-to-hero-30ab8862c40e)

## Introduction

- [Going Reactive!](https://medium.com/swlh/going-reactive-f6c22aa10597)
  - Being reactive means ability to “react” to “events/changes”. One very simple example is how a column in excel-sheet gets updated (reacts) if the column which it depends upon changes.

## Callback hell

In Java, you can encounter a similar callback hell scenario when working with reactive programming libraries like Project Reactor or RxJava. Reactive programming is often used to handle asynchronous and event-driven operations, and it can lead to nested callbacks if not used carefully. Here's an example using Project Reactor:

```shell
Mono<User> getUserById(String userId) {
    return userRepository.findById(userId)
        .flatMap(user -> {
            return orderRepository.findByUserId(user.getId())
                .flatMap(orders -> {
                    return paymentService.processPayments(orders)
                        .then(Mono.just(user));
                });
        });
}
```

In this code, we first find a user by their ID, then we find their orders, and finally, we process payments for those orders. Each step involves a callback, and if you have more operations to chain, it can become difficult to read and maintain.

To address callback hell in reactive programming, you can use a more declarative style by chaining operators. Here's the same functionality using a more readable approach with Project Reactor:

```shell
Mono<User> getUserById(String userId) {
    return userRepository.findById(userId)
        .flatMap(user -> orderRepository.findByUserId(user.getId()))
        .flatMap(orders -> paymentService.processPayments(orders))
        .thenReturn(user);
}
```

In this refactored code:

1. We chain the `flatMap` operators to handle the sequential operations.
2. The code becomes more linear and easier to read.
3. We use `thenReturn` to return the user as the final result.

This approach is more declarative and reduces the callback hell problem, making the code more maintainable and easier to understand in the context of reactive programming. The use of reactive libraries encourages this style of chaining operations, helping you avoid deeply nested callbacks.

## Workflow

The Reactive Streams workflow is a standard for asynchronous stream processing with non-blocking back pressure. It is based on four core interfaces:

* Publisher: A Publisher is responsible for producing and emitting data elements to one or more Subscribers.
* Subscriber: A Subscriber is responsible for consuming data elements from one or more Publishers.
* Subscription: A Subscription is a contract between a Publisher and a Subscriber that allows the Subscriber to request data elements from the Publisher and to cancel the subscription.
* Processor: A Processor is a combination of a Publisher and a Subscriber that can transform data elements as they pass through it.

![image](https://user-images.githubusercontent.com/22516811/268493701-3c273615-5980-4935-a87e-22bffc0f376f.png)

The Reactive Streams workflow is as follows:

1. A Subscriber calls the subscribe() method on a Publisher.
2. The Publisher returns a Subscription to the Subscriber.
3. The Subscriber can then call the request() method on the Subscription to request data elements from the Publisher.
4. The Publisher will emit data elements to the Subscriber until the Subscriber calls the cancel() method on the Subscription or the Publisher completes.
5. When the Publisher completes, it will emit an onComplete() signal to the Subscriber.
6. The Subscriber can then call the unsubscribe() method on the Subscription to release any resources associated with the subscription.

![image](https://user-images.githubusercontent.com/22516811/268493724-d79f6555-de47-4187-9455-9a4cbb1f7835.png)

**Back pressure**

Back pressure is a key feature of the Reactive Streams workflow. It allows the Subscriber to control the rate at which it receives data elements from the Publisher. This is important to prevent the Subscriber from being overwhelmed by data.

To implement back pressure, the Subscriber can call the request() method to specify the number of data elements it is willing to receive. The Publisher will then emit data elements to the Subscriber up to the requested number, or until it completes. If the Subscriber calls request() with a value of 0, the Publisher will stop emitting data elements until the Subscriber calls request() with a positive value.

**Example**

The following example shows a simple Reactive Streams workflow:

```
// Publisher
public class MyPublisher implements Publisher<String> {

    private Subscriber<String> subscriber;

    @Override
    public void subscribe(Subscriber<String> subscriber) {
        this.subscriber = subscriber;
    }

    public void emit(String element) {
        subscriber.onNext(element);
    }

    public void complete() {
        subscriber.onComplete();
    }
}

// Subscriber
public class MySubscriber implements Subscriber<String> {

    private Subscription subscription;

    @Override
    public void onNext(String element) {
        // Process the data element
    }

    @Override
    public void onComplete() {
        // The Publisher has completed
    }

    @Override
    public void onError(Throwable t) {
        // The Publisher has emitted an error
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
    }
}

// Usage
MyPublisher publisher = new MyPublisher();
MySubscriber subscriber = new MySubscriber();

publisher.subscribe(subscriber);

// Request 10 data elements
subscriber.request(10);

// Emit 10 data elements
publisher.emit("element 1");
publisher.emit("element 2");
...
publisher.emit("element 10");

// Complete the Publisher
publisher.complete();

// The Subscriber will then process the 10 data elements and call onComplete()
```
