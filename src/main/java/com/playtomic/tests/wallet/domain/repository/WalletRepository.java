package com.playtomic.tests.wallet.domain.repository;

import com.playtomic.tests.wallet.domain.model.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends MongoRepository<Wallet, String> {

}
