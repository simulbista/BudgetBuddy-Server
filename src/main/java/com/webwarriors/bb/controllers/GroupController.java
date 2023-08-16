package com.webwarriors.bb.controllers;

import java.util.Map;
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

import com.webwarriors.bb.models.Group;
import com.webwarriors.bb.services.GroupService;

@RestController
@RequestMapping(value = "/api/group")
public class GroupController {

	@Autowired
	GroupService groupService;

	// create a group
	// API end point: POST /api/group/{uid}
	@PostMapping("/{uid}")
	public ResponseEntity<String> addGroup(@PathVariable String uid, @RequestBody Group group) {
		try {
			groupService.addGroup(uid, group);
			String successMessage = "Group ".concat(group.getGName()).concat(" has been created!");
			return new ResponseEntity<String>(successMessage, HttpStatus.CREATED);
		} catch (Exception e) {
			String errorMessage = "Error creating group: " + e.getMessage();
			return new ResponseEntity<String>(errorMessage, HttpStatus.BAD_REQUEST);
		}
	}

	// get group info gid by uid (only members in the group can get the info)
	// API end point: GET /api/group/{gid}/by/{uid}
	@GetMapping("/{gid}/by/{uid}")
	public Map<String, Object> getGroupInfo(@PathVariable String gid, @PathVariable String uid) throws Exception {
		return groupService.getGroupInfo(gid, uid);
	}

	// update group
	// API end point: PUT /api/group/{uid}
	//Request body must include gid and gName
	@PutMapping("/{uid}")
	public ResponseEntity<String> updateGroup(@PathVariable String uid, @RequestBody Group group) {
		String message;
		try {
			groupService.updateGroup(uid, group);
			message = "Group with id ".concat(group.getGid()).concat(" has been updated!");
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Error updating group: " + e.getMessage();
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}
	// delete group
	// API end point: DELETE /api/group/{gid}/by/{uid}
	@DeleteMapping("/{gid}/by/{uid}")
	public ResponseEntity<String> deleteGroup(@PathVariable String gid, @PathVariable String uid) {
		String message;
		try {
			message = "Group with id ".concat(uid).concat(" has been deleted!");
			groupService.deleteGroup(gid, uid);
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Error deleting user: " + e.getMessage();
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	} 

}
