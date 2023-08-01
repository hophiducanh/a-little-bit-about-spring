To demonstrate the pros and cons of using the `Runnable` interface and the `Thread` class in Java, we will create two examples. In both examples, we will implement a simple program that prints numbers from 1 to 5 with a delay between each number.

1. Using `Runnable` interface:

```java
class MyRunnable implements Runnable {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class RunnableExample {
    public static void main(String[] args) {
        MyRunnable myRunnable = new MyRunnable();
        Thread thread1 = new Thread(myRunnable, "Thread 1");
        Thread thread2 = new Thread(myRunnable, "Thread 2");

        thread1.start();
        thread2.start();
    }
}
```

2. Using `Thread` class:

```java
class MyThread extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class ThreadExample {
    public static void main(String[] args) {
        MyThread thread1 = new MyThread();
        MyThread thread2 = new MyThread();

        thread1.start();
        thread2.start();
    }
}
```

Now let's discuss the pros and cons of each approach:

**Pros of using Runnable interface:**

1. **Better Code Reusability:** Implementing the `Runnable` interface allows you to separate the task's implementation from the thread's creation. You can use the same `Runnable` instance to create multiple threads, promoting code reusability.

2. **Flexibility with Inheritance:** Since Java supports single inheritance, using `Runnable` leaves more room for your class hierarchy to extend other classes if needed.

3. **Thread Pooling:** When using `Runnable` objects, you can easily use thread pooling by passing the `Runnable` instances to an executor service, allowing you to control the number of concurrent threads more effectively.

**Cons of using Runnable interface:**

1. **Less Convenient:** Implementing the `Runnable` interface requires creating a separate class or using an anonymous inner class, which might add boilerplate code and reduce code readability.

2. **Lack of Additional Thread Methods:** The `Runnable` interface does not provide direct access to thread-specific methods like `interrupt()` or `join()`. You need to use `Thread.currentThread()` to access these methods.

**Pros of using Thread class:**

1. **Simplicity:** The `Thread` class provides a simple way to create and manage threads directly without creating additional classes.

2. **Direct Access to Thread Methods:** As `Thread` is a direct representation of a thread, you have direct access to thread-specific methods like `interrupt()` or `join()`.

**Cons of using Thread class:**

1. **Inflexibility with Inheritance:** Extending the `Thread` class may limit your class hierarchy, as Java does not support multiple inheritance.

2. **Less Code Reusability:** Creating threads by extending the `Thread` class might lead to less code reusability since each thread is tied to a specific task.

In general, the preference between using `Runnable` and `Thread` comes down to your specific use case and design preferences. If you require code reusability or plan to use thread pooling, implementing the `Runnable` interface might be a better option. On the other hand, if you prefer simplicity and direct access to thread-specific methods, extending the `Thread` class can be more suitable.