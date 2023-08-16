import React, { useState, useEffect } from "react";
import axios from "axios";
import { Typography, Box } from "@mui/material";

const UserProfile = () => {
  const [userInfo, setUserInfo] = useState({
    nickName: "",
    email: "",
    role: "",
  });

  const uid = localStorage.getItem("uid");

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        // Make a GET request to fetch the expenses from the API endpoint
        const response = await axios.get(`./api/user/${uid}`, {
          headers: {
            // Authorization: token,
          },
        });
        //Set the retrieved  data to the component state
        setUserInfo(response.data);
      } catch (error) {
        console.error("Error fetching transactions:", error);
      }
    };
    fetchUserInfo();
  }, []);

  return (
    <div>
      <Typography variant="h4" gutterBottom padding="3rem" color="#00A03E">
        User Profile
      </Typography>
      <Box marginBottom={2}>
        <Typography variant="h6">Nickname: {userInfo.nickName}</Typography>
        <Typography variant="h6">Email: {userInfo.email}</Typography>
        <Typography variant="h6">Role: {userInfo.role}</Typography>
      </Box>
    </div>
  );
};

export default UserProfile;
