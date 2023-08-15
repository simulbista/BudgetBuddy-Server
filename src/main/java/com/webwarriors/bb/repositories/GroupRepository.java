package com.webwarriors.bb.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.webwarriors.bb.models.Group;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

	Group findBygName(String gName);

	Optional<Group> findByghid(String uid);

}
