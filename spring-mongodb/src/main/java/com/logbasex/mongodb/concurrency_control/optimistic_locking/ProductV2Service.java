package com.logbasex.mongodb.concurrency_control.optimistic_locking;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductV2Service {
	private final ProductV2Repository productV2Repo;
	
//	@Transactional
//	public void updateProductQuantity(String productId, int quantityChange) {
//		try {
//			ProductV2 productV2 = productV2Repo.findById(productId).orElse(null);
//
//			if (productV2 != null) {
//				productV2.setQuantity(productV2.getQuantity() + quantityChange);
//				productV2Repo.save(productV2);
//			}
//		} catch (OptimisticLockingFailureException ex) {
//			System.out.println("Let's retry..");
//		}
//	}
	
	@Transactional
	public void updateProductQuantity(String productId, int quantityChange) {
		int retryCount = 0;
		int maxRetries = 3;
		boolean updateSuccessful = false;
		
		while (retryCount < maxRetries && !updateSuccessful) {
			try {
				ProductV2 productV2 = productV2Repo.findById(productId).orElse(null);
				
				if (productV2 != null) {
					productV2.setQuantity(productV2.getQuantity() + quantityChange);
					productV2Repo.save(productV2);
					updateSuccessful = true;
				}
			} catch (OptimisticLockingFailureException ex) {
				// Handle concurrent update error
				retryCount++;
				// Optionally, add a delay before retrying to prevent excessive retries
			}
		}
		
		if (!updateSuccessful) {
			System.out.println("Do something else..");
		}
	}
}
