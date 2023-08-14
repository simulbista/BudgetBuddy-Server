package com.webwarriors.bb.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public Optional<User> getUserById(@PathVariable String uid) {
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
			if (user != null) {
				userService.addUser(user);
				String successMessage = "User with email".concat(user.getEmail()).concat(" has been created!");
				return new ResponseEntity<String>(successMessage, HttpStatus.CREATED);
			}
		} catch (Exception e) {
			String errorMessage = "Error adding user: " + e.getMessage();
			return new ResponseEntity<String>(errorMessage, HttpStatus.BAD_REQUEST);
		}
		return null;
	}

	// update the user
	// API end point: PUT /api/user/
	@PutMapping("/")
	public ResponseEntity<String> updateUser(@RequestBody User user) {
		String message;
		try {
			if (user != null) {
				userService.updateUser(user);
				message = "User with email ".concat(user.getEmail()).concat(" has been updated!");
				return new ResponseEntity<String>(message, HttpStatus.CREATED);
			}
		} catch (Exception e) {
			message = "Error updating user: " + e.getMessage();
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
		message = "User doesn't exist!";
		return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
	}

}
