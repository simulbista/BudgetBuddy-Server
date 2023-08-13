package com.webwarriors.bb.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/user/")
public class HomeController {

	@GetMapping("/")
	public String hello() {
		return "hello";
	}
}
