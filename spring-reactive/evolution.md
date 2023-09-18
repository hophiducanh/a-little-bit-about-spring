# [Reactive Programming: A step ahead of Functional Programming](https://www.linkedin.com/pulse/reactive-programming-step-ahead-functional-murtaza-bagwala/)

## Why do we need Reactive Approach ?

Earlier most of the applications used to be CRUD where we read/update and delete from the databases but, nowadays, we have applications which continuously process the stream of data, respond to the change in events, for example we have weather forecasting application which continuously process the forecasting data coming from the forecasting source and pops up a notification once the temperature gets changed. Similarly, we could have a Stock Trading application which basically receiving the stock prices for say Google and once the price gets lowered by certain limit then purchases the x number of shares for us.

So, the point is any kind of application which listens to the data streams and acts on it needs to be implemented through the reactive approach.

Also, these kind of applications could also be implemented using imperative approach as well, and we were trying to implement it since long but implementing such kind of applications using imperative programming is really a mess because it doesn't have in built support for asynchronous execution, responsiveness, resiliency etc. On the other side reactive programming is a subset of a functional programming which already handles the above-mentioned key factors. If you need more insight on why imperative approaches are not suitable for reactive applications then please go through this Prefer Reactive model to Imperative.

Basically , Reactive Programming is an asynchronous programming that handles your code to react the data in observable sequences . It defines the structural programming in which one component emits the data and other components which are registered to receive will propagate those changes. It is nothing but the one of the most well known design pattern that is Observer Design Pattern in which Subscriber subscribes to the observable and observable pushes the data to the subscribers.

Reactive Programming is nothing new or fancy it was there before but, only now we have realised the need of it and gave it a name. Earlier it was called as DataFlow computing.

## Where the Reactive paradigm fits in ?
Reactive programming is a nice logical step ahead to functional programming, it basically paves the way to eliminating state and mutability from your code. It basically follows the function composition, immutability and lazy evaluation(we will see all these in detail once we touch the coding part)

So let’s take a closer look at a few advantages
- Avoid the dreaded “callback hell”;
- It’s a lot simpler than regular threading;
- Has a standard mechanism for error recovery;
- Pretty straight forward and obvious way to compose asynchronous operations;
- It offers the same “API” for database access, UI, computation, network access and everything you need it to be;
- It makes a lot easier to do complex threading, synchronising work in parallel and running some code when everything is done.
- Makes concurrency almost hassle-free.
- Doing things in a more functional way leads to readable declarative code that is easier to understand, test, and debug.