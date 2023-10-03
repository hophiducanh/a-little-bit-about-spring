## [The Unfairly Unknown ForkJoinPool](https://medium.com/swlh/the-unfairly-unknown-forkjoinpool-c262777def6a)

### How does ForkJoinPool work

The design of **ForkJoinPool** is actually very simple, but at the same time it’s very efficient. It’s based on the **Divide-And-Conquer** algorithm; each task is split into subtasks until they cannot be split anymore, they get executed in parallel and once they’re all completed, the results get combined.

### The Fork-Join model

As we just mentioned, the fork-join model is a method where we split each task (**fork**) and then wait for the completion (**join**) of all these subtasks; once they’re all completed we can combine them and return a result back.

![image](https://user-images.githubusercontent.com/22516811/272279668-f5721708-fda6-446b-95f0-ba8076c1e5a2.png)

In the above figure we can see how each `task gets divided every time` **that** fork is called; in the same way, when all tasks are completed, they get **joined** and combined to generate a final result.

![image](https://user-images.githubusercontent.com/22516811/272279934-e90eabf7-507e-4037-8ce7-d2d291ebf33e.png)

### ForkJoinPool Internals

ForkJoinPool, as any pool of threads, is composed by a predefined number of threads or workers. When we create a new ForkJoinPool, the **default level of parallelism** (number of threads) will be by default the **number of available processors in our system**, a number that gets returned by the method `Runtime.availableProcessors()`. 

> Please be aware that nowadays with so much virtualization in use (Cloud VMs and Docker), your JVM in many cases won’t have as many available processors as the number of available processors in the underlying machine.

You could also create your own ForkJoinPool specifying how many threads you need; what you should keep in mind is that for **CPU-intensive tasks you’ll see no benefit in having a pool larger than the number of processors that you have available**. However, if your tasks are IO-intensive tasks (what means that they’ll be frequently waiting for IO operations to complete) you could possibly benefit from a larger pool in some cases.

Every of these worker threads has its own worker queue, a **double-ended queue** of type WorkQueue. These local queues are normally called **deques**.

![image](https://user-images.githubusercontent.com/22516811/272283501-cb4a479a-21ed-4186-b325-6878839481db.png)

![image](https://user-images.githubusercontent.com/22516811/272284076-90a114d4-9bae-4709-be83-c64616563714.png)

So the way it works is that each of these workers keeps scanning for available subtasks to be executed, the main goal is to keep worker threads as busy as possible and maximise the use of CPU cores; a thread will only block then when there are no available subtasks to run.

What happens when a worker cannot find tasks to run on its own queue? **It will try to `steal` tasks from those workers that are busier!**

This is where it gets interesting; **how does the framework guarantees that the owner of the queue and the `stealer` don’t interfere with each other if they try to grab a task at the same time?**

Well, to minimise the contention and make it in a more efficient way, **both the owner of a queue and the `stealers` grab tasks from different parts of the queue.**

To insert tasks in a queue `push()` method is used and the owner of the queue grabs a task by calling `pop()` method. So the queue is used as a stack by the owner of the queue; taking elements from the head of the stack. Something similar to what’s shown in the illustration below:

![image](https://user-images.githubusercontent.com/22516811/272285408-adae9594-a7b1-49e9-b50e-9a33d7a42837.png)

We can quickly notice that **LIFO method (Last In, First Out)** is used, why has it been designed in such a way? Wouldn’t it make more sense to process first the tasks that has been in the queue for a longer period of time?

Well, **the answer is no**. The main reason to do this is to improve performance; by **always picking the most recent task**, we **increase the chances of having the task resources still allocated in the CPU caches**; something that will boost performance considerably. This is commonly called [locality of reference](https://www.wikiwand.com/en/Locality_of_reference).

On the other hand, when a worker runs out of tasks to process it will always **take tasks from other worker’s queue `tail`** by calling `poll()` method.
In this case we follow a FIFO approach then; this is basically to reduce the contention needed to synchronise both the owner **worker** and the **stealer**.

Another very good reason to do this is because, due to the nature of these divisible tasks, **the older tasks in the queue are the most likely to provide big chunks of work**, as they probably haven’t been split yet.

![image](https://user-images.githubusercontent.com/22516811/272288480-16ac80eb-81fe-4000-b1d3-75af18c7eb78.png)

![image](https://user-images.githubusercontent.com/22516811/272288628-ca150d37-493f-4019-983f-e2d00f783429.png)

> The **push()** method adds a task to the work queue, the **pop()** method removes and returns the next task from the work queue, and the **poll()** method checks if the work queue is empty and returns the next task if it is not empty, or None if it is empty.


You will probably notice that **push()** and **pop()** methods are only called by the **owner of the queue** and **poll()** method is only called by the **worker** trying to steal work from a different worker.

**push()** and **pop()** methods are wait-free CAS (Compare-And-Swap) operations, so they’re quite performant. However, `poll()` method is not always lock free; it’ll block in those cases where the queue is almost empty as **some synchronisation will be required in order to guarantee that only the owner or the stealer pick a given task**, but not both.




