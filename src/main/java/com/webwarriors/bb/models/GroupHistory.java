package com.webwarriors.bb.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="group-history")
public class GroupHistory {
	private ObjectId ghId;
	private ObjectId gId;
	private double groupBudget;
	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;
}
