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

//public enum Role {
//	ADMIN, USER
//};

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

	@MongoId(targetType = FieldType.STRING)
	// to map POJO uid of datatype String to _id in mongo collection
	//had to use uid instead of uId since the mapping converts the field to all small case
	private String uid;
	private String nickName;
	private String email;
	// remember to encode the password later
	private String password;
	private Role role;
	private String gid;
	private boolean deleteFlag;

	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date updatedAt;

}
