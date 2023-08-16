package com.webwarriors.bb.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webwarriors.bb.models.Group;
import com.webwarriors.bb.models.User;
import com.webwarriors.bb.repositories.GroupRepository;
import com.webwarriors.bb.repositories.UserRepository;

@Service
public class GroupService {

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	UserRepository userRepository;

//	 create a group
//	 API end point: POST /api/group/{uid}
//	 the request body store data like:
//	 {
//		 gName: "Code Crafters",
//		 defaultBudget: 1000,
//		 listofUserInfo: ["pal:pb@gmail.com","sim:sim@gmail.com"]
//	 }
	public Group addGroup(String uid, String gName, Double defaultBudget, List<String> listOfUserInfo)
			throws Exception {
		String inputNickName, inputEmail = null;
		User existingUser;

		// check1:cannot create group with already existing group name
		Group foundGroup = groupRepository.findBygName(gName);
		if (foundGroup != null)
			throw new Exception("Group already exists! Please try a different group name.");

		// check2:cannot create group with non existent user
		Optional<User> optionalExistingUser = userRepository.findById(uid);
		boolean isUserExist = optionalExistingUser.isPresent();
		if (!isUserExist)
			throw new Exception("User ".concat(uid).concat(" doesn't exist!"));

		// check3:cannot let a user create more than 1 group
		// check if the user id is present in any group as the ghid
		boolean alreadyHasGroup = groupRepository.findByghid(uid).isPresent();

		if (alreadyHasGroup)
			throw new Exception("The user ".concat(uid)
					.concat(" is the group head of an existing group. Cannot create more than one group!"));

		// Create a group with required data and save it to the repo
		// save the name of the group and then uid of the user as the gid of the group
		// (the one creating the group becomes the group head)
		Group createdGroup = new Group();
		createdGroup.setGhid(uid);
		createdGroup.setGName(gName);
		createdGroup.setDefaultBudget(defaultBudget);
		groupRepository.save(createdGroup);

		// Update the group head(the one creating the group)'s gid to the just created
		// group id
		// to indicate it is a member of the group
		User groupHead = optionalExistingUser.get();
		groupHead.setGid(createdGroup.getGid());
		userRepository.save(groupHead);

		// Now we need to add all the members coming from the request body to the group
		// i.e. set their gid to the gid of the newly created group

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
			existingUser.setGid(createdGroup.getGid());
			userRepository.save(existingUser);
		}

		return createdGroup;
	}

	// get group info gid by uid (only members in the group can get the info)
	// API end point: GET /api/group/{gid}/by/{uid}
	public Map<String, Object> getGroupInfo(String gid, String uid) throws Exception {

		// check1: if the group exists
		Optional<Group> foundGroup = groupRepository.findByIdAndUndeleted(gid);
		if (!foundGroup.isPresent())
			throw new Exception("The group with id ".concat(gid).concat(" doesn't exist!"));

		// check2: if the user exists
		Optional<User> foundUser = userRepository.findById(uid);
		if (!foundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		// check3: if the user is a member of the group or not
		// by checking if the gid of the user (with uid) matches the input gid
		boolean isMember = userRepository.findById(uid).get().getGid().equals(gid);
		if (!isMember)
			throw new Exception("Unauthorized access. The user ".concat(uid).concat(" is not the member of the group ")
					.concat(gid).concat(" !"));

		double resultGroupBudget = foundGroup.get().getDefaultBudget();
		String resultGroupName = foundGroup.get().getGName();
		String resultghid = foundGroup.get().getGhid();
		String resultgid = foundGroup.get().getGid();

		// creating the following data structure (nickname and email pair) to be
		// returned
		// resultMemberNickAndEmail = ["sim:sb@gmail.com","pal:pb@gmail.com"]
		List<User> members = userRepository.findAllBygid(gid).get();
		String eachMemberNick, eachMemberEmail;
		List<String> resultMemberNickAndEmail = new ArrayList<>();
		for (User eachMemberUser : members) {
			eachMemberNick = eachMemberUser.getNickName();
			eachMemberEmail = eachMemberUser.getEmail();
			resultMemberNickAndEmail.add(eachMemberNick.concat(":").concat(eachMemberEmail));
		}

//		storing rest of the required fields other than nickname and email pair and returning it back

		Map<String, Object> searchedGroup = new HashMap<>();
		searchedGroup.put("defaultBudget", resultGroupBudget);
		searchedGroup.put("gName", resultGroupName);
		searchedGroup.put("ghid", resultghid);
		searchedGroup.put("gid", resultgid);
		searchedGroup.put("members", resultMemberNickAndEmail);

		return searchedGroup;
	}

	// update group
	// API end point: PUT /api/group/{uid}
	// the request body store data(gid,gName,listofgroupmembers) like:
//	{
//		 gid: "64dbc09b64142a3594918f5d"
//		 gName: "Code Crafters",
//		 listofUserInfo: ["pal:pb@gmail.com","sim:sim@gmail.com"]
//	}

	@Transactional(rollbackFor = Exception.class)
	public Group updateGroup(String uid, String gid, String gName, List<String> listOfUserInfo) throws Exception {

		String inputNickName, inputEmail = null;
		User toBeAddedUser;
		Optional<Group> foundGroup = groupRepository.findById(gid);
		List<User> existingMembersOfTheGroup = userRepository.findAllBygid(gid).get();

		// removing all the existing members(except for the group head) from the group
		// because we are using the new list (from request body) as the only members in
		// the group
		for (User existingMember : existingMembersOfTheGroup) {
			//if the user is not a group head, remove them
			if(existingMember.getUid()!=foundGroup.get().getGhid()){
				existingMember.setGid(null);
			}
		}

		// check1: if the group exists
		if (!foundGroup.isPresent())
			throw new Exception("The group with id ".concat(gid).concat(" doesn't exist!"));

		// check2: if the user exists
		Optional<User> foundUser = userRepository.findById(uid);
		if (!foundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		// check3: if the user is the group head
		// if uid = ghid(group head) of the group,, only group head can update the group
		// info
		if (!uid.equals(foundGroup.get().getGhid()))
			throw new Exception("Only group heads are allowed to update the group. User ".concat(uid)
					.concat(" is not the head of the group ").concat(gid).concat(" ."));

		// Retrieve the existing group data
		Group existingGroup = foundGroup.get();
		// Copy the new data(just the group name) to the existing group object,
		// preserving other fields such as createdDate and ghid
		// if this is not done, both the fields get overwritten to null (gets replaced)
		existingGroup.setGName(gName);

		// saving the input users first
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
			toBeAddedUser = userRepository.findByEmail(inputEmail);
			// if one of the users is already in the group, dont add it to the group(skip)
			// move to the next user( iteration in the loop)
			if (toBeAddedUser.getGid() != null)
				continue;

			toBeAddedUser.setGid(gid);
			userRepository.save(toBeAddedUser);
		}

		return groupRepository.save(existingGroup);
	}

	// delete group
	// API end point: DELETE /api/group/{gid}/by/{uid}
	public Group deleteGroup(String gid, String uid) throws Exception {
		// check1: if the group exists
		Optional<Group> optionalFoundGroup = groupRepository.findById(gid);
		if (!optionalFoundGroup.isPresent())
			throw new Exception("The group with id ".concat(gid).concat(" doesn't exist!"));

		// check2: if the group has not been deleted (deleteFlag=true)
		if (optionalFoundGroup.get().isDeleteFlag())
			throw new Exception("The group with id ".concat(gid).concat(" has been deleted already!"));

		Group foundGroup = optionalFoundGroup.get();

		// check3: if the user is the group head
		// if uid = ghid that group, only group head can delete the group info
		if (!uid.equals(foundGroup.getGhid()))
			throw new Exception("Only group heads are allowed to update the group. User ".concat(uid)
					.concat(" is not the head of the group ").concat(gid).concat(" ."));

		// step1: Remove group id from all the users that belong to the group
		// find all the users with this gid (i.e. users in the group) and set them to
		// null
		Optional<List<User>> optionalUsersOfTheGroup = userRepository.findBygid(gid);

		if (optionalUsersOfTheGroup.isPresent()) {
			List<User> usersOfTheGroup = optionalUsersOfTheGroup.get();

			for (User user : usersOfTheGroup) {
				user.setGid(null);
			}
			userRepository.saveAll(usersOfTheGroup);
		}

		// step2: set the delete_flag to true for the group
		foundGroup.setDeleteFlag(true);
		return groupRepository.save(foundGroup);

	}

	// check if the user is the group head
	// API end point: GET /api/group/{gid}/ishead/{uid}
	// return true if group head else false
	public boolean isGroupHead(String uid, String gid) throws Exception {
		boolean groupHeadFlag = true;
		// check1: if the group exists
		Optional<Group> optionalFoundGroup = groupRepository.findById(gid);
		if (!optionalFoundGroup.isPresent())
			throw new Exception("The group with id ".concat(gid).concat(" doesn't exist!"));

		// check2: if the user exists
		Optional<User> foundUser = userRepository.findById(uid);
		if (!foundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		// if the uid is not the ghid of the group gid, that means the user is not the
		// group head
		// so set the flag to false
		if (!uid.equals(optionalFoundGroup.get().getGhid())) {
			groupHeadFlag = false;
		}

		return groupHeadFlag;
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

}
