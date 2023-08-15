import { NavLink, useNavigate } from "react-router-dom";

// mui imports
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import AppBar from "@mui/material/AppBar";
import { styled } from "@mui/material/styles";

const StyledNavLink = styled(NavLink)`
  color: white;
  margin-right: 30px;
  text-decoration: none;
  &.active {
    font-weight: bold;
  }
`;

const Navigation = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    // Perform logout logic and remove the token from local storage
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <AppBar position="static" sx={{ backgroundColor: "#00A03E" }}>
      <Toolbar>
        <Avatar sx={{ m: 1, bgcolor: "white" }}>
          <img src="./vite.svg" alt="Logo" />
        </Avatar>
        <Typography
          variant="h6"
          component="div"
          sx={{ flexGrow: 1, textAlign: "left" }}
        >
          Budget Buddy
        </Typography>
        <nav>
          <>
            <StyledNavLink to="/personal">Personal</StyledNavLink>
            <StyledNavLink to="/group">Group</StyledNavLink>
            <StyledNavLink to="/userProfile">User Profile</StyledNavLink>
            <StyledNavLink to="/groupProfile">Group Profile</StyledNavLink>
            <Button
              onClick={handleLogout}
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
              Logout
            </Button>
          </>
        </nav>
      </Toolbar>
    </AppBar>
  );
};
export default Navigation;
