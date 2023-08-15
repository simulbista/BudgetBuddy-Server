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
public class GroupService {

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	UserRepository userRepository;

	// create a group
	// API end point: POST /api/group/{uid}
	public Group addGroup(String uid, Group group) throws Exception {
		// check1:cannot create group with already existing group name
		Group foundGroup = groupRepository.findBygName(group.getGName());
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

		// save the uid of the user creating group as the ghid (the one creating the
		// group becomes the group head)
		group.setGhid(uid);
		
		Group createdGroup = groupRepository.save(group);
		
		//also save this gid(that just got created) in the user record (since the user is a member of the group now)
		User toBeSavedUser = optionalExistingUser.get();
		toBeSavedUser.setGid(createdGroup.getGid());
		userRepository.save(toBeSavedUser);
		
		return createdGroup;
	}

	// get group info gid by uid (only members in the group can get the info)
	// API end point: GET /api/group/{gid}/by/{uid}
	public Optional<Group> getGroupInfo(String gid, String uid) throws Exception {

		// check1: if the group exists
		Optional<Group> foundGroup = groupRepository.findById(gid);
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
		return foundGroup;
	}

	// update group
	// API end point: PUT /api/group/{uid}
	public Group updateGroup(String uid, Group group) throws Exception {
		Optional<Group> foundGroup = groupRepository.findById(group.getGid());

		// check1: if the group exists
		if (!foundGroup.isPresent())
			throw new Exception("The group with id ".concat(group.getGid()).concat(" doesn't exist!"));

		// check2: if the user exists
		Optional<User> foundUser = userRepository.findById(uid);
		if (!foundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		// check3: if the user is the group head
		// if uid = ghid(group head) of the group,, only group head can update the group info
		if (!uid.equals(foundGroup.get().getGhid()))
			throw new Exception("Only group heads are allowed to update the group. User ".concat(uid)
					.concat(" is not the head of the group ").concat(group.getGid().concat(" .")));

		// Retrieve the existing group data
		Group existingGroup = foundGroup.get();
		// Copy the new data(just the group name) to the existing group object,
		// preserving other fields such as createdDate and ghid
		// if this is not done, both the fields get overwritten to null (gets replaced)
		existingGroup.setGName(group.getGName());
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
		//if uid = ghid that group, only group head can delete the group info
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

}
