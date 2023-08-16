import { useState, useEffect } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import axios from "axios";

// imports for mui
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Select from "@mui/material/Select";
import MenuItem from "@mui/material/MenuItem";
import InputLabel from "@mui/material/InputLabel";
import FormControl from "@mui/material/FormControl";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import { createTheme, ThemeProvider } from "@mui/material/styles";

const Register = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    password2: "",
    role: "",
  });

  const [validationError, setValidationError] = useState("");

  const { name, email, password, password2, role } = formData;

  const onChange = (e) => {
    setValidationError("");
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  useEffect(() => {
    if (password && password.length < 6) {
      setValidationError("Password should be at least 6 characters long!");
      return;
    } else if (password2 !== password) {
      setValidationError("Passwords must match!");
    } else {
      setValidationError("");
    }
  }, [name, email, password, password2]);

  const onSubmit = async (e) => {
    e.preventDefault();

    //don't let the user submit the registration form if there is any validation error
    if (validationError !== "") {
      return;
    }

    let config = {
      headers: {
        "Content-Type": "application/json",
      }
    };

    let data = {
      name: name,
      email: email,
      password: password,
    };

    try {
      const response = await axios.post(`/api/user/`, data, config);

      //let decodeddata = decode(response.data.token);
      //console.log(decodeddata);
      //Store something in localstorage so that we can use it in the login page to indicate successful registration
      localStorage.setItem("isJustRegistered", "true");
      // Redirect to '/flowers' after successful registration
      navigate("/login");
    } catch (e) {
      //if email already exists, display the error during registration
      if (
        e &&
        e.response &&
        e.response.data &&
        e.response.data.error &&
        e.response.data.error.length
      )
        setValidationError(e.response.data.error[0].msg);
      console.log("error ", e);
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
          <Typography component="h1" variant="h5">
            Sign up
          </Typography>
          <Box component="form" onSubmit={onSubmit} sx={{ mt: 3 }}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  type="text"
                  label="Nick Name"
                  name="name"
                  value={name}
                  onChange={(e) => onChange(e)}
                  required
                  autoFocus
                  fullWidth
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  type="email"
                  label="Email Address"
                  name="email"
                  value={email}
                  onChange={(e) => onChange(e)}
                  fullWidth
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  type="password"
                  placeholder="Password"
                  name="password"
                  value={password}
                  onChange={(e) => onChange(e)}
                  fullWidth
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  type="password"
                  placeholder="Confirm Password"
                  name="password2"
                  value={password2}
                  onChange={(e) => onChange(e)}
                  fullWidth
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <FormControl sx={{ m: 1, margin: 0, minWidth: "100%" }}>
                  <InputLabel id="demo-simple-select-helper-label">
                    Role<sup>*</sup>
                  </InputLabel>
                  <Select
                    labelId="demo-simple-select-helper-label"
                    id="demo-simple-select-helper"
                    value={role}
                    name="role"
                    required
                    onChange={(e) => onChange(e)}
                  >
                    <MenuItem disabled value="">
                      <em>Choose Role</em>
                    </MenuItem>
                    <MenuItem value={"ADMIN"}>Admin</MenuItem>
                    <MenuItem value={"USER"}>User</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
            {validationError && (
              <Typography variant="body2" color="error" align="center">
                {validationError}
              </Typography>
            )}
            <Button
              type="submit"
              onSubmit={(e) => onSubmit(e)}
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2, backgroundColor: "#00A03E" }}
            >
              Sign Up
            </Button>
          </Box>
          <p>
            Already have an account? <NavLink to="/login">Sign In</NavLink>
          </p>
        </Box>
      </Container>
    </ThemeProvider>
  );
};

export default Register;
