package com.webwarriors.bb.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.webwarriors.bb.models.GroupHistory;

@Repository
public interface GroupHistoryRepository extends MongoRepository<GroupHistory, String> {

}
