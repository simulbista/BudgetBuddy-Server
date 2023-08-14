package com.webwarriors.bb.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.webwarriors.bb.models.GroupHistory;

public interface GroupHistoryRepository extends MongoRepository<GroupHistory, ObjectId> {

}
