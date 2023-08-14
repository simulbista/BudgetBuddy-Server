package com.webwarriors.bb.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.webwarriors.bb.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

	Optional<List<User>> findAllBygid(String gId);

	User findByEmail(String email);

	User findByNickName(String nickName);

//	Optional<User> findByuId(String string);

}
