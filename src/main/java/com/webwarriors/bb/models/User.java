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

enum Role {
	ADMIN, USER
};

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")

public class User {

	@MongoId(targetType = FieldType.STRING)
	// to map POJO uId of datatype String to _id in mongo collection
	private String uid;
	private String nickName;
	private String email;
	// remember to encode the password later
	private String password;
	private Role role;
	private String gid;

	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;

}
