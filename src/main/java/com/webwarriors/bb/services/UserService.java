package com.webwarriors.bb.services;

import java.util.List;
import java.util.Optional;

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

	// get user info by uid
	// API end point: GET /api/user/{uid}
	public Optional<User> getUserByUid(String uid) {
		return userRepository.findById(uid);
	}
	
	// get all users
	// API end point: GET /api/user/all
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// get users by gId
	// API end point: GET /api/user/all/{gid}
	public Optional<List<User>> getUsersByGid(String gid) {
		return userRepository.findAllBygid(gid);
	}

	// add user
	// API end point: POST /api/user/
	public User addUser(User user) throws Exception {
		//check if email or nickname already exists in the mongo collection
		boolean isEmailExist = userRepository.findByEmail(user.getEmail())!=null;
		boolean isNickNameExist = userRepository.findByNickName(user.getNickName())!=null;
		
		//always set gid to null during user creation (since the user is not assigned to any group during creation)
		user.setGid(null);
		
		if(isEmailExist || isNickNameExist) throw new Exception("Email or nickname is already taken!");
		return userRepository.save(user);
	}

	// update the user
	// API end point: PUT /api/user/
	public User updateUser(User user) throws Exception {
		System.out.println(user.getUid());
		boolean isExist = userRepository.findById(user.getUid()).isPresent();
		if (!isExist)
			throw new Exception("User with id " + user.getUid() + " doesn't exist!");
		return userRepository.save(user);
	}

	// add the user to a group
	// API end point: POST /api/user/{uid}/group/{gid}
	public void addUserToGroup(String uid, String gid) throws Exception {
		User user = userRepository.findById(uid).orElse(null);
		Group group = groupRepository.findById(gid).orElse(null);

		if (user == null)
			throw new Exception("User with id " + uid + " doesn't exist!");
		if (group == null)
			throw new Exception("Group with id " + gid + " doesn't exist!");

		//set gid to user (so the user has joined the group)
		user.setGid(gid);
		userRepository.save(user);
	}

	// remove the user from a group
	// API end point: PUT /api/user/{uid}/group/{gid}
	public void removeUserFromGroup(String uid, String gid) throws Exception {
		User user = userRepository.findById(uid).orElse(null);
		Group group = groupRepository.findById(gid).orElse(null);

		if (user == null)
			throw new Exception("User with id " + uid + " doesn't exist!");
		if (group == null)
			throw new Exception("Group with id " + gid + " doesn't exist!");

		user.setGid(null);
		userRepository.save(user);

	}

}
