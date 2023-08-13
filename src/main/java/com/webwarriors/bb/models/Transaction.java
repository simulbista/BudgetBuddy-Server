package com.webwarriors.bb.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="transaction")
public class Transaction {
	
	@Id
	private ObjectId tId;
	private ObjectId uId;
	private double transactionAmount;
	private Date transactionDate;
	private ObjectId gId;
	//true= income, false = expense
	private boolean isIncome;
	
	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;
}
