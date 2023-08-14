package com.webwarriors.bb.models;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "groups")
public class Group {

	@MongoId(targetType = FieldType.STRING)
	// to map POJO gid of datatype String to _id in mongo collection
	// had to use gid instead of gId since the mapping converts the field to all
	// small case
	private String gId;
	private String gName;
	private String groupHeadId;

	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;
}
