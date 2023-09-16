## [A Scalable Java Thread Pool Executor](https://dzone.com/articles/scalable-java-thread-pool-executor)

Ideally, from any thread pool executor, the expectation would be the following:

- An initial set of threads (core threads pool size) created up front, to handle the load.
- If the load increases, then more threads should be created to handle the load up to max threads (Max pool size).
- If the number of threads increases beyond Max Pool Size, then queue up the tasks.
- If Bounded Queue is used, and the queue is full, then bring in some rejection policy.

The following diagram depicts the process; only initial threads are created to handle tasks (when load is very low).

![image](https://user-images.githubusercontent.com/22516811/268428364-413f8db8-89cd-4e76-98dd-fd756f8c7984.png)


As more tasks come in, more threads are created to handle the load (task queue is still empty), assuming the total number threads created is less than max pool size.

![image](https://user-images.githubusercontent.com/22516811/268428402-2def54d2-1376-47e6-b81a-17d3fb157cd8.png)

The Task Queue starts to fill if the total number of tasks is more than total number of threads (initial + extended):

![image](https://user-images.githubusercontent.com/22516811/268428419-752e72d0-2c4d-43bc-9d58-8902aff39afd.png)

Unfortunately, Java Thread Pool Executor (TPE) is biased toward queuing rather than spawning new threads, i.e., after the initial core threads get occupied, tasks gets added to queue, and after the queue reaches its limit (which would happen only for bounded queues), extra threads would be spawned. If the queue is unbounded, then extended threads won’t get spawned at all, as depicted in the following image.

![image](https://user-images.githubusercontent.com/22516811/268428461-75783e51-1a84-451a-9d69-a37403942b8e.png)

1. Initial core threads were created to handle the load.

2. Once there are more tasks than the number of core threads, the queue starts filling up to store the tasks.

3. Once the queue is filled, extended threads are created.

---

## [Thread pool executor](https://yellowcodebooks.com/2021/07/06/java-bai-50-thread-pool-tap-3-threadpoolexecutor)