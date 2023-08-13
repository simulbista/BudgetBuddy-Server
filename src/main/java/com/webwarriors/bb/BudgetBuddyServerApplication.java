package com.webwarriors.bb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
//enabling mongo
@EnableMongoRepositories
//to make sure that the created and updated date in the mongo schema get updated
@EnableMongoAuditing
public class BudgetBuddyServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetBuddyServerApplication.class, args);
	}

}
