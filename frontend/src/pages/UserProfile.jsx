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

    
    </div>
  );
};

export default UserProfile;
