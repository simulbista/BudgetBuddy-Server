import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Typography,
  TableContainer,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Button,
  Modal,
  TextField,
  Grid,
  MenuItem,
  IconButton,
} from "@mui/material";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import {
  Edit as EditIcon,
  CheckCircle as CheckCircleIcon,
} from "@mui/icons-material";
import { convertTimestampToDate } from "../utils/dateUtils";

const Group = () => {
  const navigate = useNavigate();
  // const initialData = [
  //   {
  //     category: null,
  //     createdDate: "2023-08-15T03:24:43.548+00:00",
  //     deleteFlag: false,
  //     expense: 0,
  //     gid: 64dbc09b64142a3594918f5d,
  //     income: 250,
  //     tid: "64daeffb7f309e5545a4bb9f",
  //     transactionDate: "2023-08-14T16:39:04.194+00:00",
  //     uid: "64dc57cf7214f15e7d70edcd",
  //     updatedAt: "2023-08-15T03:24:43.548+00:00",
  //   },
  // ];
  const [transactions, setTransactions] = useState([]);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1); // Default to current month
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear()); // Default to current year
  const [isBudgetEditing, setIsBudgetEditing] = useState(false);
  const [editableBudget, setEditableBudget] = useState(0);

  const uid = localStorage.getItem("uid");
  const gid = localStorage.getItem("gid");

  const handleEditBudgetClick = () => {
    setIsBudgetEditing(true);
    setEditableBudget(transactions[0].income);
  };

  // const getTodayOfSelectedMonth = () => {
  //   // Get the current date
  //   const currentDate = new Date();

  //   // Set the year and month to the selected values
  //   currentDate.setFullYear(selectedYear);
  //   currentDate.setMonth(selectedMonth - 1);

  //   // Get the timestamp of the updated date
  //   const timestamp = currentDate.getTime();

  //   return timestamp;
  // };

  const getTodayOfSelectedMonth = () => {
    // Get the current date and time
    const currentDate = new Date();
  
    // Set the year and month to the selected values
    currentDate.setFullYear(selectedYear);
    currentDate.setMonth(selectedMonth - 1);
  
    // Get the day, month, and year components
    const day = currentDate.getDate().toString().padStart(2, '0');
    const month = (currentDate.getMonth() + 1).toString().padStart(2, '0');
    const year = currentDate.getFullYear().toString();
  
    // Get the time components
    const hours = currentDate.getHours().toString().padStart(2, '0');
    const minutes = currentDate.getMinutes().toString().padStart(2, '0');
    const seconds = currentDate.getSeconds().toString().padStart(2, '0');
    const milliseconds = currentDate.getMilliseconds().toString().padStart(3, '0');
  
    // Combine components to create the formatted date and time
    const formattedDateTime = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.${milliseconds}+00:00`;
  
    return formattedDateTime;
  };
  

  const handleUpdateBudget = async () => {
    setIsBudgetEditing(false);
    try {
      const ghid = null;
      // Make a PUT request to update the monthly budget
      const updatedBudgetData = {
        date: getTodayOfSelectedMonth(),
        groupBudget: editableBudget,
      };
      await axios.post(`/api/group-history/${gid}/${ghid}`, updatedBudgetData, {
        headers: {
          // Authorization: token,
        },
      });
      fetchTransactions();
      setSnackbarOpen(true);
    } catch (error) {
      console.error("Error updating monthly budget:", error);
    }
  };

  const handleEditClick = (row) => {
    setSelectedRow({
      name: row.category,
      date: convertTimestampToDate(row.transactionDate),
      amount: row.expense == 0 ? row.income : row.expense,
      tid: row.tid,
    });
    setOpenModal(true);
  };

  const handleModalClose = () => {
    setSelectedRow(null);
    setOpenModal(false);
  };

  const handleFieldChange = (field, value) => {
    setSelectedRow((prevRow) => ({
      ...prevRow,
      [field]: value,
    }));
  };

  const handleAddNewItem = () => {
    setOpenModal(true);
    setSelectedRow({
      name: "",
      date: "",
      amount: 0,
    });
  };
 

  useEffect(() => {
    fetchTransactions(); // Initial fetching of transactions
  }, [selectedMonth, selectedYear]);

  const fetchTransactions = async () => {
    try {
      // Make a GET request to fetch the expenses from the API endpoint
      const selectedMonthStr = new Date(0, selectedMonth - 1).toLocaleString(
        "default",
        {
          month: "long",
        }
      );
      const response = await axios.get(
        `./api/transaction/month/${selectedMonthStr}/by/group/${gid}/by/${uid}`,
        {
          headers: {
          //  Authorization: token,
          },
        }
      );
      //Set the retrieved expenses data to the component state
      setTransactions(response.data);
    } catch (error) {
      console.error("Error fetching transactions:", error);
    }
  };

  const handleDeleteRow = async (transactionId) => {
    try {
      // Make a DELETE request to the remove transaction API endpoint
      await axios.delete(`/api/transaction/${transactionId}/by/${uid}`, {
        headers: {
          //Authorization: token,
        },
      });
      fetchTransactions();
      // Remove the deleted transaction from the local transaction state
      // setTransactions((prevTransactions) =>
      //   prevTransactions.filter(
      //     (transaction) => transaction.tid !== transactionId
      //   )
      // );
      setSnackbarOpen(true);
    } catch (error) {
      console.error("Error deleting transaction:", error);
    }
  };

  const handleSaveChanges = async () => {
    if (selectedRow) {
      if (selectedRow.tid !== undefined) {
        try {
          // Make a PUT request to the add transaction API endpoint
          const transcationData = {
            tid: selectedRow.tid,
            category: selectedRow.name,
            expense: selectedRow.amount,
            transactionDate: selectedRow.date,
          };
          await axios.put(`/api/transaction/${uid}`, transcationData, {
            headers: {
            //  Authorization: token,
            },
          });
          fetchTransactions();
          setSnackbarOpen(true);
        } catch (error) {
          console.error("Error updating transaction:", error);
        }
      } else {
        try {
          // Make a POST request to the add transaction API endpoint
          const transcationData = {
            category: selectedRow.name,
            expense: selectedRow.isIncome ? 0 : selectedRow.amount,
            income: selectedRow.isIncome ? selectedRow.amount : 0,
            transactionDate: selectedRow.date,
            gid: gid,
          };
          await axios.post(`/api/transaction/${uid}`, transcationData, {
            headers: {
             // Authorization: token,
            },
          });
          fetchTransactions();
          setSnackbarOpen(true);
        } catch (error) {
          console.error("Error creating transaction:", error);
        }
      }
    }
    handleModalClose();
  };

  // Calculate TotalIncome, TotalExpense, and Total

  const totalExpense = transactions.reduce(
    (sum, transaction) => sum + parseFloat(transaction.expense),
    0
  );

  const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 250,
    bgcolor: "background.paper",
    border: "2px solid #00A03E",
    boxShadow: 24,
    p: 4,
  };

  return (
    <div className="orders-page">
      <Typography variant="h4" gutterBottom padding="3rem" color="#00A03E">
        Transactions
      </Typography>
      {/* Add the dropdowns for selecting month and year */}

      <Grid container marginTop={2} marginBottom={2}>
        <Grid item xs={3}>
          <TextField
            select
            label="Month"
            value={selectedMonth}
            onChange={(e) => setSelectedMonth(e.target.value)}
          >
            {/* Create options for months */}
            {Array.from({ length: 12 }, (_, i) => i + 1).map((month) => (
              <MenuItem key={month} value={month}>
                {new Date(0, month - 1).toLocaleString("default", {
                  month: "long",
                })}
              </MenuItem>
            ))}
          </TextField>
        </Grid>
        <Grid item xs={3}>
          <TextField
            select
            label="Year"
            value={selectedYear}
            onChange={(e) => setSelectedYear(e.target.value)}
          >
            {/* Create options for years */}
            {Array.from({ length: 1 }, (_, i) => selectedYear - i).map(
              (year) => (
                <MenuItem key={year} value={year}>
                  {year}
                </MenuItem>
              )
            )}
          </TextField>
        </Grid>
        <Grid item xs={3}>
          <Box>
            <Typography variant="h6">Total Expense: </Typography>
            <Typography variant="h6" style={{ color: "red" }}>
              {totalExpense}
            </Typography>
          </Box>
        </Grid>
        {transactions.length > 0 && (
          <Grid item xs={3}>
            <Box>
              <Typography variant="h6">Monthly Budget: </Typography>
              {isBudgetEditing ? (
                <div style={{ display: "inline-flex" }}>
                  <TextField
                    type="number"
                    value={editableBudget}
                    onChange={(e) => setEditableBudget(e.target.value)}
                  />
                  <IconButton
                    sx={{ color: "#00A03E" }}
                    onClick={handleUpdateBudget}
                  >
                    <CheckCircleIcon />
                  </IconButton>
                </div>
              ) : (
                <div style={{ display: "inline-flex" }}>
                  <Typography variant="h6" style={{ color: "blue" }}>
                    {transactions[0].income}
                  </Typography>
                  <IconButton
                    sx={{ color: "#00A03E" }}
                    onClick={handleEditBudgetClick}
                  >
                    <EditIcon />
                  </IconButton>
                </div>
              )}
            </Box>
          </Grid>
        )}
      </Grid>

      {transactions.length === 0 ? (
        <Typography variant="body1">No transactions available</Typography>
      ) : (
        <TableContainer>
          <Table>
            <TableHead sx={{ backgroundColor: "lightgoldenrodyellow" }}>
              <TableRow>
                <TableCell sx={{ fontWeight: "bold" }}>Category</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Date</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Type</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Amount</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions.map((row, index) => (
                <TableRow key={row.tid}>
                  <TableCell>{row.category}</TableCell>
                  <TableCell>
                    {convertTimestampToDate(row.transactionDate)}
                  </TableCell>
                  <TableCell style={{ color: "red" }}>Expense</TableCell>
                  <TableCell>{row.expense}</TableCell>
                  <TableCell>
                    <Button
                      sx={{
                        color: "#00A03E",
                        marginLeft: "10px",
                        fontWeight: "bold",
                        backgroundColor: "#FFDB58",
                        "&:hover": {
                          backgroundColor: "#FFDB58",
                          color: "#00A03E",
                        },
                      }}
                      onClick={() => handleEditClick(row)}
                    >
                      Edit
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
                        },
                      }}
                      onClick={() => handleDeleteRow(row.tid)}
                    >
                      Delete
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      { gid != 'null' && <Button
        onClick={handleAddNewItem}
        sx={{
          color: "#00A03E",
          marginTop: "10px",
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
        + New
      </Button>}

      <Button
        variant="outlined"
        color="primary"
        sx={{
          color: "#00A03E",
          marginLeft: "10px",
          marginTop: "10px",
          fontWeight: "bold",
          backgroundColor: "#FFDB58",
          borderColor: "#FFDB58",
          "&:hover": {
            backgroundColor: "#FFDB58",
            color: "#00A03E",
            borderColor: "#FFDB58",
          },
        }}
        onClick={() => navigate("/groupProfile")}
      >
        Manage Group
      </Button>

      <Modal
        open={openModal}
        onClose={handleModalClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <Typography id="modal-modal-title" variant="h6" component="h2">
            Transaction
          </Typography>
          <Typography id="modal-modal-description" sx={{ mt: 1 }}>
            <div>
              {selectedRow && (
                <Box component="form" sx={{ mt: 3 }}>
                  <div>
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={6}>
                        <TextField
                          label="Category"
                          value={selectedRow.name}
                          onChange={(e) =>
                            handleFieldChange("name", e.target.value)
                          }
                        />
                      </Grid>
                    </Grid>
                    <Grid container spacing={2} marginTop={1}>
                      <Grid item xs={12} sm={6}>
                        <TextField
                          type="date"
                          value={selectedRow.date}
                          onChange={(e) =>
                            handleFieldChange("date", e.target.value)
                          }
                        />
                      </Grid>
                    </Grid>
                    <Grid container spacing={2} marginTop={1}>
                      <Grid item xs={12} sm={6}>
                        <TextField
                          label="Amount"
                          type="number"
                          value={selectedRow.amount}
                          onChange={(e) =>
                            handleFieldChange("amount", e.target.value)
                          }
                        />
                      </Grid>
                    </Grid>
                    <Grid container marginTop={2}>
                      <Grid item xs={12} sm={3}>
                        <Button
                          onClick={handleSaveChanges}
                          sx={{
                            color: "#00A03E",
                            fontWeight: "bold",
                            backgroundColor: "#FFDB58",
                            "&:hover": {
                              backgroundColor: "#FFDB58",
                              color: "#00A03E",
                            },
                          }}
                        >
                          Save
                        </Button>
                      </Grid>
                      <Grid item xs={12} sm={3}>
                        <Button
                          onClick={handleModalClose}
                          sx={{
                            color: "#00A03E",
                            marginLeft: "110px",
                            fontWeight: "bold",
                            backgroundColor: "#FFDB58",
                            "&:hover": {
                              backgroundColor: "#FFDB58",
                              color: "#00A03E",
                            },
                          }}
                        >
                          Cancel
                        </Button>
                      </Grid>
                    </Grid>
                  </div>
                </Box>
              )}
            </div>
          </Typography>
        </Box>
      </Modal>
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
          Transaction Update Success
        </Alert>
      </Snackbar>
    </div>
  );
};

export default Group;
