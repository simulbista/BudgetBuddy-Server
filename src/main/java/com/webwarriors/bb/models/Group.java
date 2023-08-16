package com.webwarriors.bb.models;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "groups")
public class Group {

	@MongoId(targetType = FieldType.STRING)
	// to map POJO gid of datatype String to _id in mongo collection
	// had to use gid instead of gId since the mapping converts the field to all
	// small case
	private String gid;
//	for some reason the request body key - gName is not being read, only all small case key i.e. gname was being read, so had to use the annotation
	@JsonProperty("gName")
	private String gName;
	private String ghid;
	private double defaultBudget;
	private boolean deleteFlag;

	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;
}
