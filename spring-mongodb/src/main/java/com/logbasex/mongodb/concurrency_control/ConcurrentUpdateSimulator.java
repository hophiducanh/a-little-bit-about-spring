package com.logbasex.mongodb.concurrency_control;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Database Scenario:
 * <p>
 * In the database world, write conflicts can lead to inconsistencies in data if not managed correctly. Consider the
 * following example with a MongoDB collection:
 * <p>
 * Document A: { "_id": 1, "quantity": 10 }
 * Now, two clients, Client X and Client Y, both want to increase the quantity of Document A concurrently:
 * <p>
 * Client X: Increases quantity by 2 units.
 * Client Y: Increases quantity by 3 units.
 * If the database doesn't handle concurrent writes properly, a conflict can occur:
 * <p>
 * Client X reads the current quantity (10) and increments it by 2 units (new quantity: 12).
 * However, before Client X's update is saved, Client Y also reads the current quantity (10) and increments it by 3
 * units (new quantity: 13).
 * At this point, both clients have successfully modified the data based on the version they read. However, when
 * their updates are applied, the system needs to decide whose changes to keep. This decision is often based on the
 * order of updates or other strategies like "last write wins."
 * <p>
 * In this case, if the system uses "last write wins," the final quantity would be 13, even though the intended
 * increases were 2 units and 3 units respectively. This creates an inconsistency between the intended updates and
 * the actual result, leading to write conflict.
 * <p>
 */
@Service
@SpringBootApplication
@RequiredArgsConstructor
public class ConcurrentUpdateSimulator implements CommandLineRunner {
	
	private final ProductService productSv;
	private final ProductRepository productRepo;
	
	@PostConstruct
	public void init() {
		Product product = productRepo.findById("product-1").orElse(new Product());
		product.setId("product-1");
		product.setQuantity(10);
		productRepo.save(product);
	}
	
	@Override
	public void run(String... args) throws Exception {
		String productId = "product-1";
		
		// Since both threads are executing independently, the exact order in which these threads complete is
		// non-deterministic and depends on factors such as the operating system's thread scheduling algorithm and execution times.
		Thread thread1 = new Thread(() -> productSv.updateProductQuantity(productId, 2));
		Thread thread2 = new Thread(() -> productSv.updateProductQuantity(productId, 3));
		
		// thread1.start() is called, and it starts its execution concurrently with the main thread.
		// thread2.start() is called, and it starts its execution concurrently with thread1.
		thread1.start();
		thread2.start();
		
		//The join() calls at the end ensure that the main thread waits for both thread1 and thread2 to finish their execution before continuing.
		//However, as mentioned earlier, the order in which these threads complete is not guaranteed.
		//thread1.join() is called, which means the main thread waits for thread1 to finish its execution before moving on.
		//Once thread1 completes its execution, the main thread moves to thread2.join() and waits for thread2 to finish.
		thread1.join();
		thread2.join();
		
		Product finalProduct = productRepo.findById(productId).orElse(new Product());
		System.out.println("Final Product Quantity: " + finalProduct.getQuantity());
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ConcurrentUpdateSimulator.class, args);
	}
}
