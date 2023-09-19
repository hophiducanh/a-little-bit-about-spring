package com.logbasex.reactive.controller;

import com.logbasex.reactive.dto.ProductDto;
import com.logbasex.reactive.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProductController {
	
	private final ProductService productSv;
	
	@GetMapping
	public Flux<ProductDto> getProducts(){
		return productSv.getProducts();
	}
	
	@GetMapping("/{id}")
	public Mono<ProductDto> getProduct(@PathVariable String id){
		return productSv.getProduct(id);
	}
	
	@GetMapping("/product-range")
	public Flux<ProductDto> getProductBetweenRange(@RequestParam("min") double min, @RequestParam("max")double max){
		return productSv.getProductInRange(min, max);
	}
	
	@PostMapping
	public Mono<ProductDto> saveProduct(@RequestBody Mono<ProductDto> productDtoMono){
		System.out.println("controller method called ...");
		return productSv.saveProduct(productDtoMono);
	}
	
	@PutMapping("/update/{id}")
	public Mono<ProductDto> updateProduct(@PathVariable String id, @RequestBody Mono<ProductDto> productDtoMono){
		return productSv.updateProduct(id, productDtoMono);
	}
	
	@DeleteMapping("/delete/{id}")
	public Mono<Void> deleteProduct(@PathVariable String id){
		return productSv.deleteProduct(id);
	}
}
