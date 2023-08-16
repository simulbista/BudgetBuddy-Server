package com.webwarriors.bb.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.webwarriors.bb.models.GroupHistory;

@Repository
public interface GroupHistoryRepository extends MongoRepository<GroupHistory, String> {

	// return the group history records by gid and month (latest first) - so when we
	// call this method, we get the 0 index to get the first value i.e. the latest record for that month
	@Query(value = "{ 'gid': ?0, $expr: { $eq: [ { $month: '$date' }, ?1 ] } }", sort = "{ 'date': -1 }")
	List<GroupHistory> findByGidAndMonth(String gid, int month);

}
