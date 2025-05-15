package com.playtomic.tests.wallet.infrastructure.repository;

import com.playtomic.tests.wallet.domain.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

}
