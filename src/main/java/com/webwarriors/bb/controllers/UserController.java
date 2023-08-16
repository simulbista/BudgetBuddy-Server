package com.webwarriors.bb.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webwarriors.bb.models.User;
import com.webwarriors.bb.services.UserService;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

	@Autowired
	private UserService userService;

	public String hello() {
		return "Hi from user API!";
	}

	// get user info by uId
	// API end point: GET /api/user/{uid}
	@GetMapping("/{uid}")
	public Optional<User> getUserById(@PathVariable String uid) throws Exception {
		return userService.getUserByUid(uid);
	}

	// get all users
	// API end point: GET /api/user/all
	@GetMapping("/all")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	// get users by gId
	// API end point: GET /api/user/all/{gid}
	@GetMapping("/all/{gid}")
	public Optional<List<User>> getUsersByGid(@PathVariable String gid) {
		return userService.getUsersByGid(gid);
	}

	// add user
	// API end point: POST /api/user/
	@PostMapping("/")
	public ResponseEntity<String> addUser(@RequestBody User user) {
		try {

			userService.addUser(user);
			String successMessage = "User with email".concat(user.getEmail()).concat(" has been created!");
			return new ResponseEntity<String>(successMessage, HttpStatus.CREATED);

		} catch (Exception e) {
			String errorMessage = "Error adding user: " + e.getMessage();
			return new ResponseEntity<String>(errorMessage, HttpStatus.BAD_REQUEST);
		}
	}

	// update the user
	// API end point: PUT /api/user/
	@PutMapping("/")
	public ResponseEntity<String> updateUser(@RequestBody User user) {
		String message;
		try {
			userService.updateUser(user);
			message = "User with email ".concat(user.getEmail()).concat(" has been updated!");
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Error updating user: " + e.getMessage();
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}

	}

	// add the user to a group
	// API end point: PUT /api/user/{uid}/addgroup/{gid}
	// request body should contain a list of nickname and email (mind the order) as
	// a list
	// ["sim:sb@gmail.com","pal:pb@gmail.com"]
	@PutMapping("/{uid}/addgroup/{gid}")
	public ResponseEntity<String> addUserToGroup(@PathVariable String uid, @PathVariable String gid,
			@RequestBody List<String> listOfUserInfo) {
		String message;
		try {
			message = userService.addUserToGroup(uid, gid, listOfUserInfo);
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Error updating user: " + e.getMessage();
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

	// remove the user from a group
	// API end point: PUT /api/user/{uid}/removegroup/{gid}
	@PutMapping("/{uid}/removegroup/{gid}")
	public ResponseEntity<String> removeOrLeaveUserFromGroup(@PathVariable String uid, @PathVariable String gid) {
		String message;
		try {
			message = "User with id ".concat(uid).concat(" has been added to the group ".concat(gid).concat("."));
			userService.removeOrLeaveUserFromGroup(uid, gid);
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Error updating user: " + e.getMessage();
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

	// delete the user (soft delete)
	// API end point: DELETE /api/user/{uid}
	@DeleteMapping("/{uid}")
	public ResponseEntity<String> removeUser(@PathVariable String uid) {
		String message;
		try {
			message = "User with id ".concat(uid).concat(" has been removed!");
			userService.removeUser(uid);
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Error deleting user: " + e.getMessage();
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

}
