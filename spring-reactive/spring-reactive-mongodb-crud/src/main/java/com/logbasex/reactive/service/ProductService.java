package com.logbasex.reactive.service;

import com.logbasex.reactive.dto.ProductDto;
import com.logbasex.reactive.repository.ProductRepository;
import com.logbasex.reactive.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {
	
	private final ProductRepository productRepo;
	
	public Flux<ProductDto> getProducts() {
		return productRepo
				.findAll()
				.map(AppUtils::entityToDto);
	}
	
	public Mono<ProductDto> getProduct(String id) {
		return productRepo
				.findById(id)
				.map(AppUtils::entityToDto);
	}
	
	public Flux<ProductDto> getProductInRange(double min, double max) {
		return productRepo.findByPriceBetween(Range.closed(min, max));
	}
	
	public Mono<ProductDto> saveProduct(Mono<ProductDto> productDtoMono) {
		return productDtoMono
				.map(AppUtils::dtoToEntity)
				.flatMap(productRepo::insert)
				.map(AppUtils::entityToDto);
	}
	
	public Mono<ProductDto> updateProduct(String id, Mono<ProductDto> productDtoMono) {
		return productRepo
				.findById(id)
				.flatMap(p -> productDtoMono.map(AppUtils::dtoToEntity))
				.doOnNext(e -> e.setId(id))
				.flatMap(productRepo::save)
				.map(AppUtils::entityToDto);
	}
	
	public Mono<Void> deleteProduct(String id){
		return productRepo.deleteById(id);
	}
}
