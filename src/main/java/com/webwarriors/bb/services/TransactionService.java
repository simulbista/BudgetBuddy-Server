package com.webwarriors.bb.services;

import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webwarriors.bb.models.Group;
import com.webwarriors.bb.models.Transaction;
import com.webwarriors.bb.models.User;
import com.webwarriors.bb.repositories.GroupRepository;
import com.webwarriors.bb.repositories.TransactionRepository;
import com.webwarriors.bb.repositories.UserRepository;

@Service
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;
	
	@Autowired
	GroupHistoryService ghService;

	// create a transaction (individual or group)
	// API end point: POST /api/transaction/{uid}
	// if request body has gid, then its a group transaction
	public Transaction addTransaction(String uid, Transaction transaction) throws Exception {

		// check1:if user exists
		boolean isUserExist = userRepository.findById(uid).isPresent();
		if (!isUserExist)
			throw new Exception("User ".concat(uid).concat(" doesn't exist!"));

		// if gid exists i.e if it is a group transaction, it only has expense and no
		// income
		if (transaction.getGid() != null) {
			transaction.setIncome(0);
		}
		// save uid from path variable to the transaction object to be saved
		transaction.setUid(uid);
		return transactionRepository.save(transaction);
	}

	// get transaction records by month
	// API end point: GET /api/transaction/month/{month}/by/{uid}
	// give full names of the month - e.g.: august
	public List<Transaction> getTransactionByMonth(String month, String uid) throws Exception {

		// check1: if the user exists
		Optional<User> foundUser = userRepository.findById(uid);
		if (!foundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		// convert month to respective no. for the repo method
		int monthInNumber = convertMonthToNumber(month);
		if (monthInNumber == -1)
			throw new Exception("Month ".concat(month).concat(" is invalid!"));
		// get all transactions by uid and by the month
		List<Transaction> foundTransaction = transactionRepository.findByUidAndMonth(uid, monthInNumber);
		return foundTransaction;
	}

	// get transaction records by month for a group
	// API end point: GET /api/transaction/month/{month}/by/group/{gid}/by/{uid}
	// give full names of the month - e.g.: august
	public List<Transaction> getTransactionByMonthForGroup(String month, String gid, String uid) throws Exception {

		// check1: if the user exists
		Optional<User> foundUser = userRepository.findById(uid);
		if (!foundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		// check2: if the group exists
		Optional<Group> foundGroup = groupRepository.findById(gid);
		if (!foundGroup.isPresent())
			throw new Exception("The group with id ".concat(gid).concat(" doesn't exist!"));

		// check3: if the user is a member of the group
		if (!foundUser.get().getGid().equals(gid))
			throw new Exception("Unauthorized access! The user ".concat(uid)
					.concat(" is not a member of the group ".concat(gid).concat(" .")));

		// convert month to respective no. for the repo method
		int monthInNumber = convertMonthToNumber(month);
		if (monthInNumber == -1)
			throw new Exception("Month ".concat(month).concat(" is invalid!"));
		// get all transactions by uid and by gid and month
		List<Transaction> foundTransaction = transactionRepository.findByGidAndMonth(gid, monthInNumber);
		
		//set the latest group budget for the month to the income field (so that it can be displayed back in the frontend)
		for(Transaction eachtransaction : foundTransaction) {
			//if group history doesnt have any records, so there wont be any budget
			//then we set the defaultbudget as the income to all the members of the group
			if(ghService.getLatestMonthlyGroupBudget(gid,uid, month)==0.0) {
				eachtransaction.setIncome(foundGroup.get().getDefaultBudget());
			}else {
				eachtransaction.setIncome(ghService.getLatestMonthlyGroupBudget(gid,uid, month));
			}
		}
		return foundTransaction;
	}

	// get transaction records by category
	// API end point: GET /api/transaction/category/{category}/by/{uid}
	public List<Transaction> getTransactionByCategory(String category, String uid) throws Exception {

		// check1: if the user exists
		Optional<User> foundUser = userRepository.findById(uid);
		if (!foundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		// get all transactions by uid and by category
		List<Transaction> foundTransaction = transactionRepository.findByUidAndCategory(uid, category);
		return foundTransaction;
	}

	// delete transaction
	// API end point: DELETE /api/transaction/{tid}/by{uid}
	public Transaction deleteTransaction(String tid, String uid) throws Exception {
		// check1: if the user exists
		Optional<User> optionalFoundUser = userRepository.findById(uid);
		if (!optionalFoundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		// check2: if the transaction exists
		Optional<Transaction> optionalFoundTransaction = transactionRepository.findById(tid);
		if (!optionalFoundTransaction.isPresent())
			throw new Exception("The transaction with id ".concat(tid).concat(" doesn't exist!"));

		// check3: if transaction is already deleted (deleteFlag=true)
		if (optionalFoundTransaction.get().isDeleteFlag())
			throw new Exception("The transaction with id ".concat(tid).concat(" has been deleted already!"));

		Transaction foundTransaction = optionalFoundTransaction.get();

		// set the delete_flag to true for the transaction
		foundTransaction.setDeleteFlag(true);
		return transactionRepository.save(foundTransaction);
	}

	// update transaction
	// API end point: PUT /api/transaction/{uid}
	public Transaction updateTransaction(String uid, Transaction transaction) throws Exception {
		// check1: if the user exists
		Optional<User> optionalFoundUser = userRepository.findById(uid);
		if (!optionalFoundUser.isPresent())
			throw new Exception("The user with id ".concat(uid).concat(" doesn't exist!"));

		// check2: if the transaction exists
		Optional<Transaction> optionalFoundTransaction = transactionRepository.findById(transaction.getTid());
		if (!optionalFoundTransaction.isPresent())
			throw new Exception("The transaction with id ".concat(transaction.getTid()).concat(" doesn't exist!"));

		// if the uid actually belongs to the transaction with tid
		if (!uid.equals(optionalFoundTransaction.get().getUid()))
			throw new Exception("The user ".concat(uid).concat(" is not authorized to update transaction ")
					.concat(transaction.getTid()).concat(" ."));

		// if gid exists i.e if it is a group transaction, it only has expense and no
		// income
		if (transaction.getGid() != null) {
			transaction.setIncome(0);
		}

		// Retrieve the existing transaction data
		Transaction existingTransaction = optionalFoundTransaction.get();
		// Copy the new data to the existing transaction object,
		// preserving value in other fields
		if (transaction.getTransactionDate() != null)
			existingTransaction.setTransactionDate(transaction.getTransactionDate());
		if (transaction.getIncome() != 0.0)
			existingTransaction.setIncome(transaction.getIncome());
		if (transaction.getExpense() != 0.0)
			existingTransaction.setExpense(transaction.getExpense());
		if (transaction.getCategory() != null)
			existingTransaction.setCategory(transaction.getCategory());

		return transactionRepository.save(existingTransaction);
	}

	// utility method to convert input month to its respective no. (for the repo
	// method to match later)
	public static int convertMonthToNumber(String monthName) {
		DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
		String[] months = dfs.getMonths();
		for (int i = 0; i < months.length; i++) {
			if (months[i].equalsIgnoreCase(monthName)) {
				// Month numbers are 0-based in Calendar class, so add 1
				return i + 1;
			}
		}
		return -1; // Return -1 if month name is not valid
	}
}
