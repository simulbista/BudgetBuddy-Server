package com.webwarriors.bb.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.webwarriors.bb.models.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

	//find all transactions(undeleted) by uid (with gid=null i.e. individual transaction) for given month
	@Query(value = "{ $and: [ { 'uid': ?0 }, { 'deleteFlag': false }, { 'gid': null }, { $expr: { $eq: [ { $month: '$transactionDate' }, ?1 ] } } ] }")
	List<Transaction> findByUidAndMonth(String uid, int monthInNumber);

	//find all transactions(undeleted) by uid for given category
	@Query(value = "{ $and: [ { 'uid': ?0 }, { 'category': ?1 }, { 'deleteFlag': false } ] }")
	List<Transaction> findByUidAndCategory(String uid, String category);
	
	//find all group transactions for the given month
	@Query(value = "{ $and: [ { 'gid': ?0 }, { 'deleteFlag': false }, { $expr: { $eq: [ { $month: '$transactionDate' }, ?1 ] } } ] }")
	List<Transaction> findByGidAndMonth(String gid, int monthInNumber);




}
