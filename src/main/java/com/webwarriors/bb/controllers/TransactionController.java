package com.webwarriors.bb.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.webwarriors.bb.models.Transaction;
import com.webwarriors.bb.services.TransactionService;

@RestController
@RequestMapping(value = "/api/transaction")
public class TransactionController {

	@Autowired
	TransactionService transactionService;

	// create a transaction (individual or group)
	// API end point: POST /api/transaction/{uid}
	// if request body has gid, then its a group transaction
	@PostMapping("/{uid}")
	public ResponseEntity<String> addTransaction(@PathVariable String uid, @RequestBody Transaction transaction) {
		try {
			transactionService.addTransaction(uid, transaction);
			String successMessage = "Transaction has been created!";
			return new ResponseEntity<String>(successMessage, HttpStatus.CREATED);
		} catch (Exception e) {
			String errorMessage = "Error creating group: " + e.getMessage();
			return new ResponseEntity<String>(errorMessage, HttpStatus.BAD_REQUEST);
		}
	}

	// get transaction records by month
	// API end point: GET /api/transaction/month/{month}/by/{uid}
	// give full names of the month - e.g.: august
	@GetMapping("month/{month}/by/{uid}")
	public List<Transaction> getTransactionByMonth(@PathVariable String month, @PathVariable String uid)
			throws Exception {
		return transactionService.getTransactionByMonth(month, uid);
	}

	// get transaction records by month for a group
	// API end point: GET /api/transaction/month/{month}/by/group/{gid}/by/{uid}
	// give full names of the month - e.g.: august
	@GetMapping("month/{month}/by/group/{gid}/by/{uid}")
	public List<Transaction> getTransactionByMonthForGroup(@PathVariable String month, @PathVariable String gid,
			@PathVariable String uid) throws Exception {
		return transactionService.getTransactionByMonthForGroup(month, gid, uid);
	}

	// get transaction records by category
	// API end point: GET /api/transaction/category/{category}/by/{uid}
	@GetMapping("category/{category}/by/{uid}")
	public List<Transaction> getTransactionByCategory(@PathVariable String category, @PathVariable String uid)
			throws Exception {
		return transactionService.getTransactionByCategory(category, uid);
	}

	// delete transaction
	// API end point: DELETE /api/transaction/{tid}/by{uid}
	@DeleteMapping("/{tid}/by/{uid}")
	public ResponseEntity<String> deleteTransaction(@PathVariable String tid, @PathVariable String uid) {
		String message;
		try {
			message = "Transaction with id ".concat(tid).concat(" has been deleted!");
			transactionService.deleteTransaction(tid, uid);
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Error deleting transaction: " + e.getMessage();
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

	// update transaction
	// API end point: PUT /api/transaction/{uid}
	@PutMapping("/{uid}")
	public ResponseEntity<String> updateTransaction(@PathVariable String uid, @RequestBody Transaction transaction) {
		String message;
		try {
			transactionService.updateTransaction(uid, transaction);
			message = "Transaction with id ".concat(transaction.getTid()).concat(" has been updated!");
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Error updating transaction: " + e.getMessage();
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

}
