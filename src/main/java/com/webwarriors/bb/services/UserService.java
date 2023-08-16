package com.webwarriors.bb.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.webwarriors.bb.models.Group;
import com.webwarriors.bb.models.User;
import com.webwarriors.bb.repositories.GroupRepository;
import com.webwarriors.bb.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

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

//	@Autowired
//	private BCryptPasswordEncoder passwordEncoder;

	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// add user
	// API end point: POST /api/user/
	public User addUser(User user) throws Exception {
		// check if email or nickname already exists in the mongo collection
		boolean isEmailExist = userRepository.findByEmail(user.getEmail()) != null;
		boolean isNickNameExist = userRepository.findByNickName(user.getNickName()) != null;

		// always set gid to null during user creation (since the user is not assigned
		// to any group during creation)
		user.setGid(null);

		// BCryptPassword
		user.setPassword(passwordEncoder.encode(user.getPassword()));
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
	// ["sim:sb@gmail.com","pal:pb@gmail.com"]
	public String addUserToGroup(String uid, String gid, List<String> listOfUserInfo) throws Exception {

		String inputNickName, inputEmail = null;
		User existingUser;

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
		// check4: if the user is already in the group i.e. its gid is equal to the
		// input gid
		if (user.getGid() != null && user.getGid().equals(gid)) {
			throw new Exception("User with id " + uid + " is already in the group " + gid + ".");
		}

		for (String userInfo : listOfUserInfo) {
			String[] splitUserInfo = userInfo.split(":");
			if (splitUserInfo.length != 2)
				throw new Exception(
						"The list if user info contains data in the incorrect format. Make sure its nickname:email!");
			inputNickName = splitUserInfo[0];
			inputEmail = splitUserInfo[1];
			if (!validateUser(inputNickName, inputEmail))
				throw new Exception("Invalid user!");

			// set gid to user (so the user has joined the group)

			existingUser = userRepository.findByEmail(inputEmail);
			existingUser.setGid(gid);
			userRepository.save(existingUser);
		}
		return listOfUserInfo.size() + " users have been added to the group successfully!";
	}

	// remove the user from a group
	// API end point: PUT /api/user/{uid}/removegroup/{gid}
	public void removeOrLeaveUserFromGroup(String uid, String gid) throws Exception {
		User user = userRepository.findById(uid).orElse(null);
		// to check if the group id actually exists in group collection
		Group group = groupRepository.findById(gid).orElse(null);

		// check1: if user exists
		if (user == null)
			throw new Exception("User with id " + uid + " doesn't exist!");
		// check1: if group exists
		if (group == null)
			throw new Exception("Group with id " + gid + " doesn't exist!");
		// check3: if user is the group head of the group (group head can delete) or its
		// the user trying to remove themselves
		if (!group.getGhid().equals(uid) || user.getGid().equals(gid))
			throw new Exception("Unauthorized Access! User with id " + uid
					+ " is not the head or the member of the group".concat(gid).concat(".No delete permission!"));

		// if the user has already been removed from the group i.e. gid is null
		if (user.getGid() == null)
			throw new Exception("User with id " + uid + " is not in the group " + gid + ".");

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

	// helper method to validate user before adding them to the group
	public boolean validateUser(String nick, String email) throws Exception {
		User userByNickName = userRepository.findByNickName(nick);
		User userByEmail = userRepository.findByEmail(email);
		// if both fields are empty
		if (userByNickName == null || userByEmail == null)
			throw new Exception("Provided Nickname/email does not exist!");
		// if both fields i.e. nick and email do not belong to the same user then the
		// user is invalid
		if (!userByNickName.getUid().equals(userByEmail.getUid()))
			throw new Exception("User invalid. Nickname and email should be of the same user!");
		return true;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepository.findByEmail(email);
		System.out.println(email);
		if (user == null) {
			throw new UsernameNotFoundException("No user found with email: " + email);
		}
		System.out.println(user.getPassword());
		// convert roles into a list of GrantedAuthority and pass that list to the User
		// constructor
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}
	
	public User getUserByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}
	
	

}
