package com.webwarriors.bb.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.webwarriors.bb.models.Transaction;

@Repository
public interface TransactionHistory extends MongoRepository<Transaction, ObjectId> {

}
