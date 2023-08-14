package com.webwarriors.bb.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.webwarriors.bb.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

	Optional<List<User>> findAllByGId(ObjectId gId);

}
