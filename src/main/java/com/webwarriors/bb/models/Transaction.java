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
@Document(collection = "transaction")
public class Transaction {

	@Id
	private ObjectId tId;
	private ObjectId uId;
	// if g_id exists, it means the record is a group transaction (so only expense,
	// as no income for group)
	private ObjectId gId;
	private double expense;
	private double income;
	private Date transactionDate;

	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;
}
