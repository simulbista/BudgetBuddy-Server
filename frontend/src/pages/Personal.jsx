import { useState, useEffect } from "react";
import axios from "axios";
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
  FormControlLabel,
  FormControl,
  Switch,
  Grid,
  MenuItem,
} from "@mui/material";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

const Personal = () => {
  const initialData = [
    {
      name: "Salary",
      date: "2023-08-15",
      isIncome: true,
      amount: 5000,
      tid: 1,
    },
    {
      name: "Groceries",
      date: "2023-08-14",
      isIncome: false,
      amount: 150,
      tid: 2,
    },
  ];
  const [transactions, setTransactions] = useState(initialData);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1); // Default to current month
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear()); // Default to current year

  const handleEditClick = (row) => {
    setSelectedRow(row);
    setOpenModal(true);
  };

  const handleModalClose = () => {
    setSelectedRow(null);
    setOpenModal(false);
  };

  const handleFieldChange = (field, value) => {
    setSelectedRow((prevRow) => ({
      ...prevRow,
      [field]: field === "isIncome" ? !prevRow.isIncome : value,
    }));
  };

  const handleAddNewItem = () => {
    setOpenModal(true);
    setSelectedRow({
      name: "",
      date: "",
      isIncome: true,
      amount: "",
    });
  };
  const token = localStorage.getItem("token");

  useEffect(() => {
    // Filter transactions based on selectedMonth and selectedYear
    const filteredTransactions = initialData.filter((transaction) => {
      const transactionDate = new Date(transaction.date);
      return (
        transactionDate.getMonth() + 1 === selectedMonth &&
        transactionDate.getFullYear() === selectedYear
      );
    });
    setTransactions(filteredTransactions);
  }, [selectedMonth, selectedYear]);

  useEffect(() => {
    // const fetchTransactions = async () => {
    //   try {
    //     // Make a GET request to fetch the expenses from the API endpoint
    //     const response = await axios.get(`./api/transactions`, {
    //       headers: {
    //         Authorization: token,
    //       },
    //     });
    //      Set the retrieved expenses data to the component state
    //     setTransactions(response.data);
    //   } catch (error) {
    //     console.error("Error fetching transactions:", error);
    //   }
    // };
    // fetchTransactions();
  }, []);

  const handleDeleteRow = async (transactionId) => {
    try {
      // Make a DELETE request to the remove transaction API endpoint
      // await axios.delete(`/api/transactions/${transactionId}`, {
      //   headers: {
      //     Authorization: token,
      //   },
      // });

      // Remove the deleted transaction from the local transaction state
      setTransactions((prevTransactions) =>
        prevTransactions.filter(
          (transaction) => transaction.tid !== transactionId
        )
      );
      setSnackbarOpen(true);
    } catch (error) {
      console.error("Error deleting transaction:", error);
    }
  };

  const handleSaveChanges = () => {
    if (selectedRow) {
      if (selectedRow.tid !== undefined) {
        // Editing existing row
        const newData = transactions.map((row) =>
          row.tid === selectedRow.tid ? selectedRow : row
        );
        setTransactions(newData);
      } else {
        // Adding new row
        setTransactions([...transactions, selectedRow]);
      }
    }
    handleModalClose();
  };

  // Calculate TotalIncome, TotalExpense, and Total
  const totalIncome = transactions
    .filter((transaction) => transaction.isIncome)
    .reduce((sum, transaction) => sum + parseFloat(transaction.amount), 0);

  const totalExpense = transactions
    .filter((transaction) => !transaction.isIncome)
    .reduce((sum, transaction) => sum + parseFloat(transaction.amount), 0);

  const total = totalIncome - totalExpense;

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

      <Grid container marginTop={2}>
        <Grid item>
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
        <Grid item>
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
          <Box margin={1}>
            <Typography variant="h6">Total Income: </Typography>
            <Typography variant="h6" style={{ color: "green" }}>
              {totalIncome}
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={3}>
          <Box marginTop={2}>
            <Typography variant="h6">Total Expense: </Typography>
            <Typography variant="h6" style={{ color: "red" }}>
              {totalExpense}
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={3}>
          <Box marginTop={2}>
            <Typography variant="h6">Savings: </Typography>
            <Typography
              variant="h6"
              style={{ color: total >= 0 ? "green" : "red" }}
            >
              {total}
            </Typography>
          </Box>
        </Grid>
      </Grid>

      {transactions.length === 0 ? (
        <Typography variant="body1">No transactions available</Typography>
      ) : (
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow sx={{backgroundColor:"lightgoldenrodyellow"}}>
                <TableCell sx={{ fontWeight: "bold" }}>Name</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Date</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Type</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Amount</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions.map((row, index) => (
                <TableRow key={index}>
                  <TableCell>{row.name}</TableCell>
                  <TableCell>{row.date}</TableCell>
                  <TableCell style={{ color: row.isIncome ? "green" : "red" }}>
                    {row.isIncome ? "Income" : "Expense"}
                  </TableCell>
                  <TableCell>{row.amount}</TableCell>
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
                      onClick={() => handleDeleteRow(index)}
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
      <Button
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
                          label="Name"
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
                        <FormControl component="fieldset">
                          <FormControlLabel
                            control={
                              <Switch
                                checked={selectedRow.isIncome}
                                onChange={(e) =>
                                  handleFieldChange("isIncome", e.target.value)
                                }
                                name="isIncome"
                              />
                            }
                            label="Is Income"
                          />
                        </FormControl>
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

export default Personal;
