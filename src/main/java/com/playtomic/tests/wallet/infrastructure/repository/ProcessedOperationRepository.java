package com.playtomic.tests.wallet.infrastructure.repository;

import com.playtomic.tests.wallet.domain.model.ProcessedOperation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedOperationRepository extends MongoRepository<ProcessedOperation, String> {

}
