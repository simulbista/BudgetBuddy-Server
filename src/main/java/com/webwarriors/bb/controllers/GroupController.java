package com.webwarriors.bb.controllers;

import java.util.List;
import java.util.Map;

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

import com.webwarriors.bb.services.GroupService;

@RestController
@RequestMapping(value = "/api/group")
public class GroupController {

	@Autowired
	GroupService groupService;

//	 create a group
//	 API end point: POST /api/group/{uid}
//	 the request body store data like:
//	 {
//		 gName: "Code Crafters",
//		 defaultBudget: 1000,
//		 listofUserInfo: ["pal:pb@gmail.com","sim:sim@gmail.com"]
//	 }
	@PostMapping("/{uid}")
	public ResponseEntity<String> addGroup(@PathVariable String uid,
			@RequestBody Map<String, Object> gNameWithMembers) {
		// destructuring groupName and members
		String gName = (String) gNameWithMembers.get("gName");

		// converting input default budget datatype from integer(if so) to double
		Double inputDefaultBudget = 0.0;
		Object rawDefaultBudget = gNameWithMembers.get("defaultBudget");
		if (rawDefaultBudget != null) {
			if (rawDefaultBudget instanceof Number) {
				inputDefaultBudget = ((Number) rawDefaultBudget).doubleValue();
			}
		}

		// default budget is 0.0 when not provided
		double defaultBudget = 0.0;
		if (inputDefaultBudget != null) {
			defaultBudget = inputDefaultBudget;
		}

		@SuppressWarnings("unchecked")
		List<String> listOfUserInfo = (List<String>) gNameWithMembers.get("listofUserInfo");
		try {
			groupService.addGroup(uid, gName, defaultBudget, listOfUserInfo);
			String successMessage = "Group ".concat(gName).concat(" has been created!");
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
	// the request body store data(gid,gName,listofgroupmembers) like:
//	{
//		 gid: "64dbc09b64142a3594918f5d"
//		 gName: "Code Crafters",
//		 listofUserInfo: ["pal:pb@gmail.com","sim:sim@gmail.com"]
//	}
	@PutMapping("/{uid}")
	public ResponseEntity<String> updateGroup(@PathVariable String uid,
			@RequestBody Map<String, Object> groupDetailsForUpdate) {
		// destructuring groupName and members
		String gid = (String) groupDetailsForUpdate.get("gid");
		String gName = (String) groupDetailsForUpdate.get("gName");
		@SuppressWarnings("unchecked")
		List<String> listOfUserInfo = (List<String>) groupDetailsForUpdate.get("listofUserInfo");
		String message;
		try {
			groupService.updateGroup(uid, gid, gName, listOfUserInfo);
			message = "Group with id ".concat(gid).concat(" has been updated!");
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

	// check if the user is the group head
	// API end point: GET /api/group/{gid}/ishead/{uid}
	// return true if group head else false
	@GetMapping("/{gid}/ishead/{uid}")
	public boolean isGroupHead(@PathVariable String uid, @PathVariable String gid) throws Exception {
		return groupService.isGroupHead(uid, gid);
	}

}
