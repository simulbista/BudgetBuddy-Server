package com.webwarriors.bb.services;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webwarriors.bb.models.Group;
import com.webwarriors.bb.models.User;
import com.webwarriors.bb.repositories.GroupRepository;
import com.webwarriors.bb.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GroupRepository groupRepository;

	// get user info by uId
	// API end point: GET /api/user/{uId}
	private Optional<User> getUserByUid(ObjectId uId) {
		return userRepository.findById(uId);
	}

	// get user info by gId
	// API end point: GET /api/user/all/{gId}
	private Optional<List<User>> getUsersByGid(ObjectId gId) {
		return userRepository.findAllByGId(gId);
	}

	// add user
	// API end point: POST /api/user/
	private User addUser(User user) {
		return userRepository.save(user);
	}

	// update the user by uId
	// API end point: PUT /api/user/{uId}
	private User updateUserByUid(User user) throws Exception {
		boolean isExist = userRepository.findAll().stream().anyMatch(u -> u.getUId().equals(user.getUId()));
		if (!isExist)
			throw new Exception("User with id " + user.getUId() + " doesn't exist!");
		return userRepository.save(user);
	}

	// add the user to a group
	// API end point: POST /api/user/{uId}/group/{gId}
	private void addUserToGroup(ObjectId uId, ObjectId gId) throws Exception {
		User user = userRepository.findById(uId).orElse(null);
		Group group = groupRepository.findById(gId).orElse(null);

		if (user == null)
			throw new Exception("User with id " + uId + " doesn't exist!");
		if (group == null)
			throw new Exception("Group with id " + gId + " doesn't exist!");

		user.setGId(gId);
		userRepository.save(user);
	}

	// remove the user from a group
	// API end point: PUT /api/user/{uId}/group/{gId}
	private void removeUserFromGroup(ObjectId uId, ObjectId gId) throws Exception {
		User user = userRepository.findById(uId).orElse(null);
		Group group = groupRepository.findById(gId).orElse(null);

		if (user == null)
			throw new Exception("User with id " + uId + " doesn't exist!");
		if (group == null)
			throw new Exception("Group with id " + gId + " doesn't exist!");

		user.setGId(null);
		userRepository.save(user);

	}

}
