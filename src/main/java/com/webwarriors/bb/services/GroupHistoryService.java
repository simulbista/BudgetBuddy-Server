package com.webwarriors.bb.services;

import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webwarriors.bb.models.GroupHistory;
import com.webwarriors.bb.repositories.GroupHistoryRepository;
import com.webwarriors.bb.repositories.GroupRepository;
import com.webwarriors.bb.repositories.UserRepository;

@Service
public class GroupHistoryService {

	@Autowired
	GroupHistoryRepository groupHistoryRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	UserRepository userRepository;

	// create a group history (i.e. group info for the current month and the future)
	// API end point: POST /api/group-history/{gid}/{ghid}
	// request body - date(from drop down) and budget
	public GroupHistory addGroupHistory(String gid, String ghid, GroupHistory groupHistory) throws Exception {

		// check1:if group(gid) exists
		boolean isGroupExist = groupRepository.findById(gid).isPresent();
		if (!isGroupExist)
			throw new Exception("Group ".concat(gid).concat(" doesn't exist!"));

		// check2: if ghid is the group head of group(gid)
		boolean isGroupHeadOfGroup = groupRepository.findById(gid).get().getGhid().equals(ghid);
		if (!isGroupHeadOfGroup)
			throw new Exception(
					"Unauthorized access! The user uid is not the group head of group ".concat(gid).concat("."));

		// since gid doesn't come from request body but comes from the path variable, we
		// store it in group history before saving
		groupHistory.setGid(gid);
		return groupHistoryRepository.save(groupHistory);
	}

	// get latest budget by month (to be displayed as the total group budget for
	// that month)
	// API end point: GET /api/group-history/{gid}/{uid}/{month}
	// month should be full name like august
	public Double getLatestMonthlyGroupBudget(String gid, String uid, String month) throws Exception {

		// check1:if group(gid) exists
		boolean isGroupExist = groupRepository.findById(gid).isPresent();
		if (!isGroupExist)
			throw new Exception("Group ".concat(gid).concat(" doesn't exist!"));

		// check2: if uid is the group member of group(gid)
		boolean isMemberOfGroup = userRepository.findById(uid).get().getGid().equals(gid);
		if (!isMemberOfGroup)
			throw new Exception(
					"Unauthorized access! The user uid is not a member of the group ".concat(gid).concat("."));

		// convert month to respective no. for the repo method
		int monthInNumber = convertMonthToNumber(month);
		if (monthInNumber == -1)
			throw new Exception("Month ".concat(month).concat(" is invalid!"));

		// returns all the group history records for that month - sorted latest first
		List<GroupHistory> existingGroupHistory = groupHistoryRepository.findByGidAndMonth(gid, monthInNumber);
		
		//if existingGroupHistory is empty (meaning there are no group history records)
		//which means we can use the defaulBudget value from Group
		if (existingGroupHistory.isEmpty()) {
			return groupRepository.findById(gid).get().getDefaultBudget();
		}

		// get the latest record which is on the 0 index
		GroupHistory latestGroupHistoryForTheMonth = existingGroupHistory.get(0);
		return latestGroupHistoryForTheMonth.getGroupBudget();
	}

	// utility method to convert input month to its respective no. (for the repo
	// method's parameter)
	public static int convertMonthToNumber(String monthName) {
		DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
		String[] months = dfs.getMonths();
		for (int i = 0; i < months.length; i++) {
			if (months[i].equalsIgnoreCase(monthName)) {
				// Month numbers are 0-based in Calendar class, so add 1
				return i + 1;
			}
		}
		return -1; // Return -1 if month name is not valid
	}

}
