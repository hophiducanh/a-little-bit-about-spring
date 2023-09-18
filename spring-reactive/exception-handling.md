## [Exception and Errors](https://kalpads.medium.com/error-handling-with-reactive-streams-77b6ec7231ff)
Before we dive deep we need to be clear about a few concepts. Exceptions and Errors are not unfamiliar to any Java developer. However, those two represent very different meanings.

**Exceptions** : An exception indicates conditions that an application might want to catch and the application might be able to recover from this condition gracefully.

**Error**: An Error indicates serious problems that an application should not try to catch or recover from. For an example OutOfMemoryError.

The same rules apply to reactive programming world as well, however since everything is a signal (whether it is a successful event or error) people use Errors when they refer to error signals in reactive streams. Hence do not get confused when we refer error signals as errors, these are not java.lang.Error s.

**Error signals are first class citizens in reactive world.**

> Any error signal in reactive sequence is a terminal event

Meaning it will stop the sequence. Even if an error-handling operator is used, it does not allow the original sequence to continue. This is a really important fact that we need to understand while using reactive streams.

## [Error Recovery Mechanism in Reactive Programming](https://www.linkedin.com/pulse/reactive-programming-step-ahead-functional-murtaza-bagwala/)

Imperative programming are very bad at handling the exceptions and recovery for example lets say you are trying to book a flight from xyz website, and you have entered the source, the destination and date of travel and the moment you hit the enter, at the backend a new thread will be created to serve your request, you are waiting for the list of the flights to get displayed but due to some error at the backend your web page is stuck. Now, a normal end user will just refresh the page again and the new thread will be created again to serve the new request and it again goes into the error loop so, this is how multithreading becomes a mess and only results in resource wastage.

In Imperative programming we propagate the exceptions **upstream**, we throw the exceptions to the caller method until it reaches to the root, here we treat exceptions as second class citizen that means if everything is alright we return results otherwise throw an exception, no method wants to take the responsibility to handle the exception, they are just passing it to the root function and if exceptions are not handled properly that would result in real unresponsiveness which we just saw in last example.

![image](https://user-images.githubusercontent.com/22516811/268646422-d7434029-82c5-47e7-8780-af776a1102fa.png)

Reactive programming treats errors as first class citizen and gives proper status to them. Reactive programming sends error to the **downstream** unlike Imperative Programming. Observables in RP are connected to the Subscribers through 3 channels :-

- **Data Channel** :- Observable passes actual data to the subscriber through this channel.
- **Complete channel**:- If data is completed then Observable sends the complete signal to the subscriber through this channel after that, no data will be flown through the **Data Channel**.
- **Error Channel**:- If any error occurs that will be passed through this channel after that, no data will flown through Data channel

![image](https://user-images.githubusercontent.com/22516811/268646813-fc640204-bdfc-48d6-b769-6af757fc0d38.png)