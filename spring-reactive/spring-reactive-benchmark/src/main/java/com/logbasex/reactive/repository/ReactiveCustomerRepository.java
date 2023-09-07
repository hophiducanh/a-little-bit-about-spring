package com.logbasex.reactive.repository;

import com.logbasex.reactive.dto.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveCustomerRepository extends ReactiveMongoRepository<Customer, String> {

}
