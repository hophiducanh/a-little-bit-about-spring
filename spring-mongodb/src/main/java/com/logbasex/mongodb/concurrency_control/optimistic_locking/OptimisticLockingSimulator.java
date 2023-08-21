package com.logbasex.mongodb.concurrency_control.optimistic_locking;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@SpringBootApplication
@RequiredArgsConstructor
public class OptimisticLockingSimulator implements CommandLineRunner {
	
	private final ProductV2Service productV2Sv;
	private final ProductV2Repository productV2Repo;
	
	@PostConstruct
	public void init() {
		ProductV2 productV2 = productV2Repo.findById("productV2-1").orElse(new ProductV2());
		productV2.setId("productV2-1");
		productV2.setQuantity(10);
		productV2Repo.save(productV2);
	}
	
	@Override
	public void run(String... args) throws Exception {
		String productId = "productV2-1";
		
		Thread thread1 = new Thread(() -> productV2Sv.updateProductQuantity(productId, 2));
		Thread thread2 = new Thread(() -> productV2Sv.updateProductQuantity(productId, 3));
		
		thread1.start();
		thread2.start();
		
		thread1.join();
		thread2.join();
		
		ProductV2 finalProduct = productV2Repo.findById(productId).orElse(new ProductV2());
		System.out.println("Final Product Quantity: " + finalProduct.getQuantity());
	}
	
	public static void main(String[] args) {
		SpringApplication.run(OptimisticLockingSimulator.class, args);
	}
}
