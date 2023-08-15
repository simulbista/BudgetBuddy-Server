package com.webwarriors.bb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
//enabling mongo
@EnableMongoRepositories
//to make sure that the created and updated date in the mongo schema get updated
@EnableMongoAuditing
public class BudgetBuddyServerApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(BudgetBuddyServerApplication.class, args);
	}
	
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index.html");
	}

}
