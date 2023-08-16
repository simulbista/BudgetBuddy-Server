import { useState, useEffect } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import axios from "axios";

// mui libraries
import { Typography } from "@mui/material";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Box from "@mui/material/Box";
import Container from "@mui/material/Container";
import { createTheme, ThemeProvider } from "@mui/material/styles";

const Login = () => {
  const navigate = useNavigate();

  // state variable to check if user registered successfully and redirected here
  //if yes we will display a message else no
  const [isJustRegistered, setIsJustRegistered] = useState(false);

  useEffect(() => {
    let localIsRegistered = localStorage.getItem("isJustRegistered");
    if (localIsRegistered === "true") {
      //storing the true value in a local variable (local to the login file)
      setIsJustRegistered(true);
      // reset the localStorage variable back to false
      localStorage.setItem("isJustRegistered", "false");
    } else {
      localIsRegistered = "false";
    }
  });
  //state to store login error
  const [loginError, setloginError] = useState("");

  //state variables for form data
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const { email, password } = formData;

  const onChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    //remove the msg about registration successful when the login form is submitted
    setIsJustRegistered(false);
    let config = {
      headers: {
        "Content-Type": "application/json",
      },
      withCredentials: true
    };

    let data = {
      email: email,
      password: password,
    };

    try {
      const response = await axios.post(`/api/auth`, data, config);
      // console.log(response.data);
      localStorage.setItem("nick", response.data.nickName);
      localStorage.setItem("uid", response.data.uid);
      localStorage.setItem("gid", response.data.gid);
      //console.log(decode(response.data.token));
      navigate("/personal");
    } catch (e) {
      setloginError("Invalid Credentials!");
      console.log(e);
    }
  };

  const defaultTheme = createTheme();
  return (
    <ThemeProvider theme={defaultTheme}>
      <Container component="main" maxWidth="xs">
        <CssBaseline />
        <Box
          sx={{
            marginTop: 8,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: "white" }}>
            <img src="./vite.svg" alt="Logo" />
          </Avatar>

          <Typography sx={{ m: 2 }} component="h1" variant="h5">
            Sign In to Budget Buddy
          </Typography>
          {/* <p>Sign Into Your Account</p> */}
          <Box
            component="form"
            action="/api/auth"
            method="POST"
            onSubmit={(e) => onSubmit(e)}
            sx={{ mt: 3, width: 300 }}
          >
            <div>
              <TextField
                margin="normal"
                fullWidth
                label="Email Address"
                autoFocus
                type="email"
                placeholder="Email Address"
                name="email"
                value={email}
                onChange={(e) => onChange(e)}
              />
            </div>
            <div>
              <TextField
                margin="normal"
                fullWidth
                label="Password"
                type="password"
                placeholder="Password"
                name="password"
                // minLength={6}
                value={password}
                onChange={(e) => onChange(e)}
              />
            </div>
            {/* display registration success msg if the user is redirected to the login page after successful registration */}
            {isJustRegistered && (
              <Typography
                variant="body2"
                sx={{ color: "green" }}
                align="center"
              >
                Registration Successful!
              </Typography>
            )}
            {loginError && (
              <Typography variant="body2" color="error" align="center">
                {loginError}
              </Typography>
            )}
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2, backgroundColor: "#00A03E" }}
            >
              Sign In
            </Button>
          </Box>
          <p>
            <NavLink to="/register">Register</NavLink>
          </p>
        </Box>
      </Container>
    </ThemeProvider>
  );
};

export default Login;
