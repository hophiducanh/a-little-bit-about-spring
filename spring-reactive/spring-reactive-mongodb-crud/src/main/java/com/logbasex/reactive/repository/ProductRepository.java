package com.logbasex.reactive.repository;

import com.logbasex.reactive.dto.ProductDto;
import com.logbasex.reactive.entity.Product;
import org.springframework.data.domain.Range;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
	
	Flux<ProductDto> findByPriceBetween(Range<Double> price);
}
