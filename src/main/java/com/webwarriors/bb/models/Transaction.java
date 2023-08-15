package com.webwarriors.bb.models;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions")
public class Transaction {

	@MongoId(targetType = FieldType.STRING)
	// to map POJO tid of datatype String to _id in mongo collection
	// had to use tid instead of tId since the mapping converts the field to all
	// small case
	private String tid;
	private String uid;
	// if g_id exists, it means the record is a group transaction (so only expense,
	// as no income for group)
	private String gid;
	private double expense;
	private double income;
	private Date transactionDate;
	private String category;
	private boolean deleteFlag;

	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;
}
