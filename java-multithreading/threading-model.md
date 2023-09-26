## [Thread Allocation Model](https://www.baeldung.com/java-outofmemoryerror-unable-to-create-new-native-thread)

An OS typically has two types of threads – user threads (threads created by a Java application) and kernel threads. User threads are supported above the kernel threads and the kernel threads are managed by the OS.

Between them, there are three common relationships:

- Many-To-One – Many user threads map to a single kernel thread
- One-To-One – One user thread map to one kernel thread
- Many-To-Many – Many user threads multiplex to a smaller or equal number of kernel threads

## [Why must user threads be mapped to a kernel thread](https://www.geeksforgeeks.org/why-must-user-threads-be-mapped-to-a-kernel-thread/)


## [Java’s Thread Model and Golang Goroutine](https://medium.com/@genchilu/javas-thread-model-and-golang-goroutine-f1325ca2df0c)

## [Cooperative và Preemptive trong Multi-tasking](https://viblo.asia/p/018-cooperative-va-preemptive-trong-multi-tasking-Qpmleeqklrd)