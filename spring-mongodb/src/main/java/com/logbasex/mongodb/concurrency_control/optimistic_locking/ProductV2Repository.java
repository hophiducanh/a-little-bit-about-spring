package com.logbasex.mongodb.concurrency_control.optimistic_locking;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductV2Repository extends MongoRepository<ProductV2, String> {

}
