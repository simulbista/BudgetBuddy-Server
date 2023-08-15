package com.webwarriors.bb.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.webwarriors.bb.models.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

	@Query(value = "{ $and: [ { 'uid': ?0 }, { $expr: { $eq: [ { $month: '$transactionDate' }, ?1 ] } } ] }")
	List<Transaction> findByUidAndMonth(String uid, int monthInNumber);
	
	@Query(value = "{ $and: [ { 'uid': ?0 }, { 'category': ?1 } ] }")
	List<Transaction> findByUidAndCategory(String uid, String category);

}
