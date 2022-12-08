## [Multi-threading vs Concurrency](https://www.educative.io/blog/multithreading-and-concurrency-fundamentals)

> **Concurrency** is the ability of your program to deal (not doing) with many things at once and is achieved through multithreading. Do not confuse **concurrency** with **parallelism** which is about doing many things at once.

## [Does a single core CPU allow only parallel execution?](https://stackoverflow.com/questions/61608659/does-a-single-core-cpu-allow-only-parallel-execution)

> **Parallel**: Two executions at the same time. 
> 
> **Concurrent**: Two executions at interleaved times. 

## [Can any computer (multi or single core) run many threads at the same time](https://stackoverflow.com/questions/41377983/can-any-computer-multi-or-single-core-run-many-threads-at-the-same-time)

> You can create multiple threads in your environment and have a **single CPU core** execute these threads. However, their execution will not be in parallel but **merely concurrent**. In other words, the CPU will execute one thread (in part or completely) first then the other thread. If there was work left on the first thread it then may return to the first thread again and execute some more instructions. The threads are thus progressing concurrently. If the threads were executed on **more than one CPU core** then they can be executed at the same time which means they are executed in **parallel**.

> In summary, multiple threads on a single CPU core can be executed concurrently. Multiple threads on multiple CPU cores can be executed concurrently or in parallel.

## Maximum number of thread per process

- https://stackoverflow.com/a/67682091/10393067
- https://stackoverflow.com/questions/344203/maximum-number-of-threads-per-process-in-linux
- https://www.baeldung.com/linux/max-threads-per-process
- [Understanding the differences between pid_max, ulimit -u and thread_max](https://unix.stackexchange.com/a/136855/363633)
    > cat /proc/sys/kernel/threads-max
  >
