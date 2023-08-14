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

enum Role {
	ADMIN, USER
};

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")

public class User {

	@Id
	private ObjectId uId;
	private String nickName;
	private String email;
	// remember to encode the password later
	private String password;
	private Role role;
	private ObjectId gId;

	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;

}
