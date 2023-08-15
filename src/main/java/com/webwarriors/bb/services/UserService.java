package com.webwarriors.bb.services;

import java.sql.Date;
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
	public Optional<User> getUserByUid(String uid) throws Exception {
		Optional<User> foundUser = userRepository.findById(uid);
		if (!foundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		return foundUser;
	}

	// get all users
	// API end point: GET /api/user/all
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// get users by gId
	// API end point: GET /api/user/all/{gid}
	public Optional<List<User>> getUsersByGid(String gid) {
		System.out.println(gid);
		return userRepository.findAllBygid(gid);
	}

	// add user
	// API end point: POST /api/user/
	public User addUser(User user) throws Exception {
		// check if email or nickname already exists in the mongo collection
		boolean isEmailExist = userRepository.findByEmail(user.getEmail()) != null;
		boolean isNickNameExist = userRepository.findByNickName(user.getNickName()) != null;

		// always set gid to null during user creation (since the user is not assigned
		// to any group during creation)
		user.setGid(null);

		if (isEmailExist || isNickNameExist)
			throw new Exception("Email or nickname is already taken!");
		return userRepository.save(user);
	}

	// update the user
	// API end point: PUT /api/user/
	public User updateUser(User user) throws Exception {
		Optional<User> foundUser = userRepository.findById(user.getUid());
		boolean isExist = foundUser.isPresent();
		if (!isExist)
			throw new Exception("User with id " + user.getUid() + " doesn't exist!");

		// since when updating the user record, the createdDate in mongo goes to null
		// we manually set all the fields except for the created Date (which was set
		// when the user was first created)
		User existingUser = foundUser.get();
		if (user.getNickName() != null)
			existingUser.setNickName(user.getNickName());
		if (user.getEmail() != null)
			existingUser.setEmail(user.getEmail());
		if (user.getPassword() != null)
			existingUser.setPassword(user.getPassword());
		if (user.getRole() != null)
			existingUser.setRole(user.getRole());
		if (user.getGid() != null)
			existingUser.setGid(user.getGid());
		existingUser.setUpdatedAt(new Date(System.currentTimeMillis()));
		return userRepository.save(existingUser);
	}

	// add the user to a group
	// API end point: PUT /api/user/{uid}/addgroup/{gid}
	// request body should contain a list of nickname and email (mind the order) as
	// a list
	// ["sim", "sb@gmail.com"]
	public User addUserToGroup(String uid, String gid, List<String> loginDetail) throws Exception {
		String inputNickName = loginDetail.get(0);
		String inputEmail = loginDetail.get(1);

		User user = userRepository.findById(uid).orElse(null);
		Group group = groupRepository.findById(gid).orElse(null);

		// check1: if user exists
		if (user == null)
			throw new Exception("User with id " + uid + " doesn't exist!");
		// check1: if group exists
		if (group == null)
			throw new Exception("Group with id " + gid + " doesn't exist!");
		// check3: if user is the group head of the group (only group head can add)
		if (!group.getGhid().equals(uid))
			throw new Exception("Unauthorized Access! User with id " + uid
					+ " is not the admin of the group".concat(gid).concat(".No add permission!"));
		// check4: if the email and nickname coming from the request body as loginDetail
		// belong to the same user (user validation)
		User userByNickName = userRepository.findByNickName(inputNickName);
		User userByEmail = userRepository.findByEmail(inputEmail);
		if (userByNickName == null || userByEmail == null)
			throw new Exception("User Validation failed! The user email or nick name doesn't exist!");
		if (!userByNickName.getUid().equals(userByEmail.getUid()))
			throw new Exception(
					"User Validation failed! The user email and nick name in the request body don't belong to the same user.");

		// set gid to user (so the user has joined the group)
		user.setGid(gid);
		return userRepository.save(user);
	}

	// remove the user from a group
	// API end point: PUT /api/user/{uid}/removegroup/{gid}
	public void removeUserFromGroup(String uid, String gid) throws Exception {
		User user = userRepository.findById(uid).orElse(null);
		// to check if the group id actually exists in group collection
		Group group = groupRepository.findById(gid).orElse(null);

		// check1: if user exists
		if (user == null)
			throw new Exception("User with id " + uid + " doesn't exist!");
		// check1: if group exists
		if (group == null)
			throw new Exception("Group with id " + gid + " doesn't exist!");
		// check3: if user is the group head of the group (only group head can delete)
		if (!group.getGhid().equals(uid))
			throw new Exception("Unauthorized Access! User with id " + uid
					+ " is not the head of the group".concat(gid).concat(".No delete permission!"));

		user.setGid(null);
		userRepository.save(user);
	}

	// delete the user (soft delete)
	// API end point: DELETE /api/user/{uid}
	public User removeUser(String uid) throws Exception {
		// check if the user exists
		Optional<User> optionalUser = userRepository.findById(uid);

		if (!optionalUser.isPresent())
			throw new Exception("User doesn't exist!");

		User user = optionalUser.get();
		user.setDeleteFlag(true);

		// Save the updated user with the deleteFlag set to true
		User updatedUser = userRepository.save(user);

		return updatedUser;
	}

}
