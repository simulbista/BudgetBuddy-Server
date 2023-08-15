package com.webwarriors.bb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webwarriors.bb.models.GroupHistory;
import com.webwarriors.bb.services.GroupHistoryService;

@RestController
@RequestMapping(value = "/api/group-history")
public class GroupHistoryController {

	@Autowired
	GroupHistoryService ghService;

	// create a group history (i.e. group info for the current month and the future)
	// API end point: POST /api/group-history/{gid}/{ghid}
	// request body - date(from drop down) and budget
	@PostMapping("/{gid}/{ghid}")
	public ResponseEntity<String> addGroupHistory(@PathVariable String gid, @PathVariable String ghid,
			@RequestBody GroupHistory groupHistory) {
		try {
			ghService.addGroupHistory(gid, ghid, groupHistory);
			return new ResponseEntity<>("Group history added successfully!", HttpStatus.CREATED);
		} catch (Exception e) {
			String errorMessage = "Error adding group history: " + e.getMessage();
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		}
	}

	// get latest budget by month (to be displayed as the total group budget for
	// that month)
	// API end point: GET /api/group-history/{gid}/{uid}/{month}
	//month should be full name like august
	@GetMapping("/{gid}/{uid}/{month}")
	public Double getLatestMonthlyGroupBudget(@PathVariable String gid, @PathVariable String uid,
			@PathVariable String month) throws Exception {
		return ghService.getLatestMonthlyGroupBudget(gid, uid, month);
	}

}
