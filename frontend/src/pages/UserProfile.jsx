import React, { useState, useEffect } from "react";
import { Typography, Button, Modal, TextField, Box } from "@mui/material";

const UserProfile = () => {
  const [userInfo, setUserInfo] = useState({
    nickname: "JohnDoe",
    email: "john@example.com",
    role: "User",
  });

  const [modalOpen, setModalOpen] = useState(false);
  const [editedNickname, setEditedNickname] = useState(userInfo.nickname);
  const [editedEmail, setEditedEmail] = useState(userInfo.email);

  const handleOpenModal = () => {
    setModalOpen(true);
    setEditedNickname(userInfo.nickname);
    setEditedEmail(userInfo.email);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
  };

  const handleSaveProfile = () => {
    setUserInfo({
      ...userInfo,
      nickname: editedNickname,
      email: editedEmail,
    });
    handleCloseModal();
  };

  useEffect(() => {
    // const fetchUserInfo = async () => {
    //   try {
    //     // Make a GET request to fetch the expenses from the API endpoint
    //     const response = await axios.get(`./api/user`, {
    //       headers: {
    //         Authorization: token,
    //       },
    //     });
    //      Set the retrieved  data to the component state
    //     setUserInfo(response.data);
    //   } catch (error) {
    //     console.error("Error fetching transactions:", error);
    //   }
    // };
    // fetchUserInfo();
  }, []);

  return (
    <div>
      <Typography variant="h4" gutterBottom padding="3rem" color="#00A03E">
        User Profile
      </Typography>
      <Box marginBottom={2}>
        <Typography variant="h6">Nickname: {userInfo.nickname}</Typography>
        <Typography variant="h6">Email: {userInfo.email}</Typography>
        <Typography variant="h6">Role: {userInfo.role}</Typography>
      </Box>
      <Button
        variant="outlined"
        color="primary"
        onClick={handleOpenModal}
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
      >
        Edit Profile
      </Button>
      <Modal open={modalOpen} onClose={handleCloseModal}>
        <Box
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 300,
            bgcolor: "background.paper",
            border: "2px solid #00A03E",
            boxShadow: 24,
            p: 4,
          }}
        >
          <Typography variant="h6" component="h2">
            Edit Profile
          </Typography>
          <Box mt={2}>
            <TextField
              label="Nickname"
              fullWidth
              value={editedNickname}
              onChange={(e) => setEditedNickname(e.target.value)}
            />
          </Box>
          <Box mt={2}>
            <TextField
              label="Email"
              fullWidth
              value={editedEmail}
              onChange={(e) => setEditedEmail(e.target.value)}
            />
          </Box>
          <Box mt={2} display="flex" justifyContent="flex-end">
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
              onClick={handleSaveProfile}
            >
              Save
            </Button>
            <Button
              variant="outlined"
              color="secondary"
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
          </Box>
        </Box>
      </Modal>
    </div>
  );
};

export default UserProfile;
