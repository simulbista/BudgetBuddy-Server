import React, { useState, useEffect } from "react";
import {
  Typography,
  Button,
  Modal,
  TextField,
  Paper,
  List,
  ListItem,
  ListItemText,
} from "@mui/material";

const GroupProfile = () => {
  const [groupInfo, setGroupInfo] = useState({
    groupName: "",
    members: [],
    groupBudget: 0.0,
  });

  const [modalOpen, setModalOpen] = useState(false);
  const [groupNameInput, setGroupNameInput] = useState("");
  const [numMembersToAdd, setNumMembersToAdd] = useState(0);
  const [memberInputs, setMemberInputs] = useState([]);
  const [groupBudgetInput, setGroupBudgetInput] = useState(0.0);

  useEffect(() => {
    // const fetchGroupInfo = async () => {
    //   try {
    //     // Make a GET request to fetch the expenses from the API endpoint
    //     const response = await axios.get(`./api/group`, {
    //       headers: {
    //         Authorization: token,
    //       },
    //     });
    //      Set the retrieved  data to the component state
    //     setGroupInfo(response.data);
    //   } catch (error) {
    //     console.error("Error fetching transactions:", error);
    //   }
    // };
    // fetchGroupInfo();
  }, []);

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

  const handleSaveGroupInfo = () => {
    setGroupInfo({
      groupName: groupNameInput,
      members: memberInputs.filter((input) => input.trim() !== ""),
      groupBudget: parseFloat(groupBudgetInput),
    });
    handleCloseModal();
  };

  const handleEditGroup = () => {
    setGroupNameInput(groupInfo.groupName);
    setNumMembersToAdd(groupInfo.members.length);
    setMemberInputs(groupInfo.members);
    handleOpenModal();
  };

  const handleLeaveGroup = () => {
    setGroupInfo({
      groupName: "",
      members: [],
    });
  };

  return (
    <div>
      <Typography variant="h4" gutterBottom padding="3rem" color="#00A03E">
        Group Profile
      </Typography>
      {groupInfo.groupName && (
        <Typography variant="h6">Group Name: {groupInfo.groupName}</Typography>
      )}
      {groupInfo.groupBudget !== 0 && (
        <Typography variant="h6">
          Group Budget: {groupInfo.groupBudget}
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
      {groupInfo.groupName && (
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
      {!groupInfo.groupName && (
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
          <TextField
            type="number"
            label="Group Budget"
            value={groupBudgetInput}
            required
            onChange={(e) => setGroupBudgetInput(parseFloat(e.target.value))}
            fullWidth
            margin="normal"
          />
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
            <TextField
              key={index}
              label={`Member ${index + 1}`}
              value={memberInputs[index] || ""}
              onChange={(e) =>
                handleAddMemberInputChange(index, e.target.value)
              }
              fullWidth
              margin="normal"
            />
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
