## Thread memory model

In Java, memory management involves several components, including the stack and heap, especially when it comes to multithreading. Let's explore each of these components in the context of Java threads:

### Stack
- Each thread in a Java application has its own stack memory.
- The stack is a region of memory used for method call frames and local variables.
- When a method is called, a new frame is pushed onto the thread's stack, which includes information about the method's parameters, local variables, and return address.
- The stack is typically faster to access than the heap because it follows a Last-In, First-Out (LIFO) structure.
- Stack memory is limited in size and typically smaller than the heap. It's determined by the JVM and may vary between implementations.

In the context of multithreading:
- Each thread has its own stack, allowing it to execute methods independently.
- This isolation helps prevent data corruption among threads, as each thread's stack is separate.

### Heap
- The heap is a region of memory used for dynamic memory allocation.
- Objects created in Java are stored in the heap.
- The heap is larger and less predictable in size compared to the stack.
- Garbage collection is responsible for reclaiming memory occupied by objects that are no longer referenced, preventing memory leaks.

In the context of multithreading:
- The heap is shared among all threads in a Java application.
- This means multiple threads can access and modify objects in the heap concurrently, which can lead to thread safety issues if proper synchronization mechanisms are not used.
- It's important to manage shared data in the heap carefully to avoid race conditions and other concurrency-related problems.

When working with multithreading in Java, it's crucial to consider the following:

1. Thread Safety: Ensure that shared data accessed by multiple threads is properly synchronized to avoid data corruption and race conditions.

2. Thread Local Variables: Use thread-local variables (e.g., `ThreadLocal` in Java) when you need per-thread storage without sharing data.

3. Stack Efficiency: The stack is generally more efficient for managing method call frames and local variables. Use the heap for objects with longer lifetimes and shared data.

4. Garbage Collection: Understand how garbage collection works in Java, as it affects the cleanup of memory used by objects on the heap.

In summary, both the stack and heap play important roles in memory management for Java threads. The stack is used for method call frames and local variables and is thread-specific, while the heap is used for object storage and is shared among all threads. Proper synchronization and memory management practices are essential when working with multithreaded Java applications.

----

## [Multithreading: Java Memory Model](https://viblo.asia/p/multithreading-java-memory-model-l0rvmm4QvyqA)

### 1. The Internal Java Memory Model

**Java Memory Model** được sử dụng trong trong các JVM chia bộ nhớ thành 2 thành phần **Thread Stacks** và **Heap**. Biểu đồ sau minh họa **Java Memory Model** từ góc độ logic

![image](https://user-images.githubusercontent.com/22516811/271992171-559a9184-f373-4289-a056-16e32645b39f.png)

- Mỗi **Thread** khi chạy trên JVM sẽ có một **Thread Stack** riêng. **Thread Stack** chứa các thông tin về các methods mà thread đã gọi khi thực thi. Khi các thread thực thi mã của nó, các stack sẽ thay đổi.


- **Thread Stack** cũng bao gồm tất cả các **local variables** của mỗi method được execute (all methods on the call stack). Một thread có thể chỉ có quyền truy cập tới thread stack của nó. Local variables được tạo bởi thread chỉ available với chính Thread tạo ra nó. Ngay cả khi nếu **2 Threads** cùng thực thi một đoạn code giống nhau thì **2 Threads** vẫn tạo ra các local variables riêng bên trong **Thread Stack**. Do đó, mỗi thread có một version cho các **local variables**.

Tất cả các **local variables** có kiểu dữ liệu gốc (boolean, byte, short, char, int, long, float, double) được lưu trữ đầy đủ trên **Thread Stack** và không khả dụng với các thread khác. Một Thread có thể pass một bản copy của primitive variable sang một Thread khác, Nhưng nó lại ko thể chia sẻ primitive local variable của chính nó.

- **Heap** chứa tất cả các objects được tạo ra bởi Java application. **Gồm có các object versions của kiểu primitive** (e.g. Byte, Integer, Long etc.). Không thành vấn đề nếu một object được tạo ra và gán cho một local variable, hoặc tạo ra như là một member variable của đối tượng khác, các object vẫn được lưu trên heap.

![image](https://user-images.githubusercontent.com/22516811/271993576-5cf872e8-e691-44af-8c86-32b1cdc6ff02.png)
