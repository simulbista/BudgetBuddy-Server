import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Button from "@mui/material/Button";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

const UserProfile = () => {
  const [user, setUser] = useState(null);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = localStorage.getItem("token");
        const { data: response } = await axios.get(
          `./api/user/${u_id}`,
          {
            headers: {
              Authorization: `${token}`,
            },
          }
        );
        setUser(response);
      } catch (error) {
        console.error(error);
      }
    };

    fetchUserData();
  }, [u_id]);

  const handleUpdateUser = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.post(
        `./api/carts`,
        {
            f_id: flower?.f_id,
            quantity: parseInt(qty),
            name: flower?.name,
            price: flower?.price
        },
        {
          headers: {
            Authorization: token,
          },
        }
      );
      // Handle the response here, such as displaying a success message 
      console.log("User details updated:", response.data);
      setSnackbarOpen(true);
      navigate("/userProfile");
    } catch (error) {
      console.error("Error updating user details:", error);
    }
  };

  return (
    <>
      {user ? (
        <div className="details">
          <div className="details-image">
            <img
              src={`/images/flowers-${flower.f_id}.jpg`}
              alt="flowerDetail"
            ></img>
          </div>
          <div className="details-info">
            <ul>
              <li>
                <h4>{flower.name}</h4>
              </li>
              <li>{flower.category}</li>
              <li>
                <b>${flower.price}</b>
              </li>
              <li>
                Description:
                <div>{flower.description}</div>
              </li>
            </ul>
          </div>
          <div className="details-action">
            <ul>
              <li>
                Price: <b>{flower.price} $</b>
              </li>
              <li>Status: {flower.stock > 0 ? "In Stock" : "Unavailable"}</li>
              <li>
                Qty:{" "}
                <select
                  value={budget}
                  onChange={(e) => {
                    setBudget(e.target.value);
                  }}
                >
                  {[...Array(flower.stock).keys()].map((x) => (
                    <option key={x + 1} value={x + 1}>
                      {x + 1}
                    </option>
                  ))}
                </select>
              </li>
              <li>
                {flower.stock > 0 && (
                  <Button
                    variant="contained"
                    sx={{ backgroundColor: "#00A03E" }}
                    onClick={handleUpdateBudget}
                    className="button"
                  >
                    Update Budget
                  </Button>
                )}
              </li>
            </ul>
          </div>
        </div>
      ) : (
        <p>Loading...</p>
      )}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={() => setSnackbarOpen(false)}
      >
        <Alert
          onClose={() => setSnackbarOpen(false)}
          severity="success"
          sx={{ width: "100%" }}
        >
          Flower added to cart!
        </Alert>
      </Snackbar>
    </>
  );
};

export default UserProfile;