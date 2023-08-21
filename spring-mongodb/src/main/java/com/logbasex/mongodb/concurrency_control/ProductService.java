package com.logbasex.mongodb.concurrency_control;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepo;
	
	public void updateProductQuantity(String productId, int quantityChange) {
		Product product = productRepo.findById(productId).orElse(null);
		if (product != null) {
			product.setQuantity(product.getQuantity() + quantityChange);
			productRepo.save(product);
		}
	}
}
