import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  Typography,
  Button,
  Modal,
  TextField,
  Paper,
  List,
  ListItem,
  ListItemText,
  IconButton,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";

const GroupProfile = () => {
  const [groupInfo, setGroupInfo] = useState({
    gName: "",
    members: [],
    defaultBudget: 0.0,
  });

  const [modalOpen, setModalOpen] = useState(false);
  const [groupNameInput, setGroupNameInput] = useState("");
  const [numMembersToAdd, setNumMembersToAdd] = useState(0);
  const [memberInputs, setMemberInputs] = useState([]);
  const [groupBudgetInput, setGroupBudgetInput] = useState(0.0);

  // const uid = "64dc57cf7214f15e7d70edcd";
  const uid = localStorage.getItem("uid");
  const gid = localStorage.getItem("gid");

  useEffect(() => {
    fetchGroupInfo();
  }, []);

  const fetchGroupInfo = async () => {
    if (gid) {
      try {
        // Make a GET request to fetch the expenses from the API endpoint
        // const response = await axios.get(`./api/group/${groupInfo.gid}/by/${uid}`, {
          const response = await axios.get(`./api/group/${gid}/by/${uid}`, {
          headers: {
            // Authorization: token,
          },
        });
        // Set the retrieved  data to the component state
        setGroupInfo(response.data);
      } catch (error) {
        console.error("Error fetching transactions:", error);
      }
    }
  };

  const handleDeleteMember = (index) => {
    const updatedMemberInputs = [...memberInputs];
    updatedMemberInputs.splice(index, 1);
    setMemberInputs(updatedMemberInputs);
    setNumMembersToAdd(updatedMemberInputs.length);
  };

  const handleOpenModal = () => {
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setGroupNameInput("");
    setNumMembersToAdd(0);
    setMemberInputs([]);
  };

  const handleAddMemberInputChange = (index, value) => {
    const updatedMemberInputs = [...memberInputs];
    updatedMemberInputs[index] = value;
    setMemberInputs(updatedMemberInputs);
  };

  const handleSaveGroupInfo = async () => {
    if (groupInfo.gid) {
      try {
        // Make a PUT request to the add transaction API endpoint
        const groupData = {
          gid: groupInfo.gid,
          gName: groupNameInput,
          listofUserInfo: memberInputs,
        };
        await axios.put(`/api/group/${uid}`, groupData, {
          headers: {
            // Authorization: token,
          },
        });
        fetchGroupInfo();
        handleCloseModal();
      } catch (error) {
        console.error("Error updating group:", error);
      }
    } else {
      try {
        // Make a POST request to the add transaction API endpoint
        const groupData = {
          gName: groupNameInput,
          defaultBudget: groupBudgetInput,
          listofUserInfo: memberInputs,
        };
        const response = await axios.post(`/api/group/${uid}`, groupData, {
          headers: {
            // Authorization: token,
          },
        });
        localStorage.setItem("gid",response.data.gid);
        fetchGroupInfo();
        handleCloseModal();
      } catch (error) {
        console.error("Error creating group:", error);
      }
    }
  };

  const handleEditGroup = () => {
    setGroupNameInput(groupInfo.gName);
    setNumMembersToAdd(groupInfo.members.length);
    setMemberInputs(groupInfo.members);
    handleOpenModal();
  };

  const handleLeaveGroup = async () => {
    try {
      // Make a POST request to the add transaction API endpoint
      const groupData = {
        gName: groupInfo.gName,
        gid: groupInfo.gid,
      };
      await axios.put(`/api/user/${uid}/removegroup/${gid}`, groupData, {
        headers: {
          // Authorization: token,
        },
      });
      fetchGroupInfo();
      setGroupInfo({
        gName: "",
        members: [],
        defaultBudget: 0.0,
      });
      handleCloseModal();
    } catch (error) {
      console.error("Error leaving group:", error);
    }
  };

  return (
    <div>
      <Typography variant="h4" gutterBottom padding="3rem" color="#00A03E">
        Group Profile
      </Typography>
      {groupInfo.gName && (
        <Typography variant="h6">Group Name: {groupInfo.gName}</Typography>
      )}
      {groupInfo.defaultBudget !== 0 && (
        <Typography variant="h6">
          Group Budget: {groupInfo.defaultBudget}
        </Typography>
      )}
      {groupInfo.members.length > 0 && (
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            height: "100%",
          }}
        >
          <List>
            <Typography variant="h6">Members:</Typography>
            {groupInfo.members.map((member, index) => (
              <ListItem key={index}>
                <ListItemText primary={member} />
              </ListItem>
            ))}
          </List>
        </div>
      )}
      {groupInfo.gid && (
        <div>
          <Button
            variant="outlined"
            color="primary"
            sx={{
              color: "#00A03E",
              marginLeft: "10px",
              fontWeight: "bold",
              backgroundColor: "#FFDB58",
              "&:hover": {
                backgroundColor: "#FFDB58",
                color: "#00A03E",
                borderColor: "#FFDB58",
              },
            }}
            onClick={handleEditGroup}
          >
            Edit Group
          </Button>
          <Button
            sx={{
              color: "#00A03E",
              marginLeft: "10px",
              fontWeight: "bold",
              backgroundColor: "#FFDB58",
              "&:hover": {
                backgroundColor: "#FFDB58",
                color: "#00A03E",
                borderColor: "#FFDB58",
              },
            }}
            variant="outlined"
            color="secondary"
            onClick={handleLeaveGroup}
          >
            Leave Group
          </Button>
        </div>
      )}
      {!groupInfo.gid && (
        <Button
          variant="outlined"
          color="primary"
          sx={{
            color: "#00A03E",
            marginLeft: "10px",
            fontWeight: "bold",
            backgroundColor: "#FFDB58",
            "&:hover": {
              backgroundColor: "#FFDB58",
              color: "#00A03E",
              borderColor: "#FFDB58",
            },
          }}
          onClick={handleOpenModal}
        >
          Add Group
        </Button>
      )}
      <Modal open={modalOpen} onClose={handleCloseModal}>
        <Paper
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            p: 4,
          }}
        >
          <Typography variant="h6">Add Group</Typography>
          <TextField
            label="Group Name"
            value={groupNameInput}
            onChange={(e) => setGroupNameInput(e.target.value)}
            fullWidth
            margin="normal"
          />
          {groupInfo.defaultBudget == 0 && (
            <TextField
              type="number"
              label="Group Budget"
              value={groupBudgetInput}
              required
              onChange={(e) => setGroupBudgetInput(parseFloat(e.target.value))}
              fullWidth
              margin="normal"
            />
          )}
          <TextField
            type="number"
            label="Number of Members to Add"
            value={numMembersToAdd}
            required
            onChange={(e) => setNumMembersToAdd(parseInt(e.target.value))}
            fullWidth
            margin="normal"
          />
          {Array.from({ length: numMembersToAdd }).map((_, index) => (
            <div key={index} style={{ display: "flex", alignItems: "center" }}>
              <TextField
                label={`Member ${index + 1}`}
                value={memberInputs[index] || ""}
                onChange={(e) =>
                  handleAddMemberInputChange(index, e.target.value)
                }
                fullWidth
                margin="normal"
              />
              <IconButton
                color="error"
                aria-label="delete"
                onClick={() => handleDeleteMember(index)}
              >
                <DeleteIcon />
              </IconButton>
            </div>
          ))}
          <Button
            variant="contained"
            color="primary"
            sx={{
              color: "#00A03E",
              marginRight: 2,
              marginLeft: "10px",
              fontWeight: "bold",
              backgroundColor: "#FFDB58",
              "&:hover": {
                backgroundColor: "#FFDB58",
                color: "#00A03E",
                borderColor: "#FFDB58",
              },
            }}
            onClick={handleSaveGroupInfo}
          >
            Save
          </Button>
          <Button
            variant="outlined"
            color="primary"
            sx={{
              color: "#00A03E",
              marginLeft: "10px",
              fontWeight: "bold",
              backgroundColor: "#FFDB58",
              "&:hover": {
                backgroundColor: "#FFDB58",
                color: "#00A03E",
                borderColor: "#FFDB58",
              },
            }}
            onClick={handleCloseModal}
          >
            Cancel
          </Button>
        </Paper>
      </Modal>
    </div>
  );
};

export default GroupProfile;
