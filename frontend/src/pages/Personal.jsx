import { useState, useEffect } from "react"
import axios from "axios";
import {
  Typography,
  Box,
  List,
  ListItem,
  ListItemText,
  Button,
} from "@mui/material";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

import './Group.css';

const Personal = () => {

  const [expenses, setExpenses] = useState([]);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        // Make a GET request to fetch the expenses from the API endpoint
        const response = await axios.get(`./api/expenses`,{
          headers: {
            Authorization: token,
          },
        });
        
        // Set the retrieved expenses data to the component state
        setExpenses(response.data);
      } catch (error) {
        console.error("Error fetching expenses:", error);
      }
    };
  
    fetchOrders();
  }, []);

  const handleCancel = async(orderId) => {
    try {
      // Make a PUT request to the cancel order API endpoint
      await axios.put(`${import.meta.env.VITE_SERVER_PORT}/api/orders/${orderId}/cancel`, null, {
        headers: {
          Authorization: token,
        },
      });
      
      // Update the status of the canceled order locally
      setExpenses((prevOrders) =>
        prevOrders.map((order) => {
          if (order._id === orderId) {
            return { ...order, status: "Cancelled" };
          }
          return order;
        })
      );
      setSnackbarOpen(true);
    } catch (error) {
      console.error("Error canceling order:", error);
    }
  };

  const handleRemove = async(orderId) => {
    try {
      // Make a DELETE request to the remove order API endpoint
      await axios.delete(`${import.meta.env.VITE_SERVER_PORT}/api/orders/${orderId}`,{
        headers: {
          Authorization: token,
        },
      });
      
      // Remove the deleted order from the local orders state
      setExpenses((prevOrders) =>
        prevOrders.filter((order) => order._id !== orderId)
      );
      setSnackbarOpen(true);
    } catch (error) {
      console.error("Error removing order:", error);
    }
  };

  const calculateTotalPrice = (flowers) => {
    let totalPrice = 0;
    flowers.forEach((flower) => {
      totalPrice += flower.quantity * flower.price;
    });
    return totalPrice.toFixed(2);
  };

  return (
    <div className="orders-page">
      <Typography variant="h4" gutterBottom>
        Expenses
      </Typography>
      {orders.length === 0 ? (
        <Typography variant="body1">No expenses available</Typography>
      ) : (
        <List>
          {expenses.map((order) => (
            <ListItem key={order.orderId} className="order-item">
              <ListItemText
                primary={`Order ID: ${order.orderId}`}
                secondary={
                  <List className="flower-list">
                    {order.flowers.map((flower) => (
                      <ListItem key={flower.id} className="flower-item">
                        <ListItemText
                          primary={flower.name}
                          secondary={
                            <Box>
                              <Typography variant="body2" component="span">
                                Quantity: {flower.quantity}
                              </Typography>
                              <br />
                              <Typography variant="body2" component="span">
                                Price: ${flower.price}
                              </Typography>
                            </Box>
                          }
                        />
                      </ListItem>
                    ))}
                  </List>
                }
              />
              &nbsp;<ListItemText primary={`Status: ${order.status}`} className="status-column"  /> 
              <ListItemText primary={` Order Total: ${calculateTotalPrice(order.flowers)} $`} className="status-column"  />
              {order.status !== "Complete" && order.status !== "Cancelled" && (
                <Button
                  variant="contained"
                  color="error"
                  onClick={() => handleCancel(order._id)}>
                  Cancel Order
                </Button>
              )}
              {(order.status == "Complete" ||  order.status == "Cancelled" ) && (
                <Button
                  variant="contained"
                  color="primary"
                  onClick={() => handleRemove(order._id)}>
                  Remove Order
                </Button>
              )}
            </ListItem>
          ))}
        </List>
      )}
      <Snackbar open={snackbarOpen} autoHideDuration={3000} onClose={() => setSnackbarOpen(false)}>
          <Alert onClose={() => setSnackbarOpen(false)} severity="success" sx={{ width: '100%' }}>
          Order Update Success
          </Alert>
        </Snackbar>
    </div>
  );
};

export default Personal;