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
@Document(collection = "group-history")
public class GroupHistory {

	@MongoId(targetType = FieldType.STRING)
	// to map POJO ghid of datatype String to _id in mongo collection
	// had to use ghid instead of ghId since the mapping converts the field to all
	// small case
	private String ghid;
	private String gid;
	private double groupBudget;
	private boolean deleteFlag;
	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;
}
