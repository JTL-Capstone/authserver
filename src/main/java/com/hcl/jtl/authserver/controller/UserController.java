package com.hcl.jtl.authserver.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.jtl.authserver.entity.User;
import com.hcl.jtl.authserver.exception.ObjectAlreadyExistsException;
import com.hcl.jtl.authserver.exception.ObjectNotExistsException;
import com.hcl.jtl.authserver.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService userService;

	@GetMapping("/welcome")
	public ResponseEntity<String> sayWelcome() {
		logger.info("Entering sayWelcome()");
		return new ResponseEntity<>("Welcome to Microservice Capstone Project for Auth Service", HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		logger.info("getAllUsers Response --> " + userService.getAllUsers().toString());
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<User> createUser(@RequestBody User user) throws ObjectAlreadyExistsException {
		logger.info("Entering createStockDetails()");
		return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<String> deleteUserByUserId(@PathVariable String userId) {
		try {
			userService.deleteUserByUserId(userId);
			logger.info(userId + " is deleted successffully...!");
			return new ResponseEntity<>(userId + " is deleted successffully...!", HttpStatus.OK);
		} catch (ObjectNotExistsException e) {
			logger.error("error in fetching user details for " + userId);
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/{userId}")
	public ResponseEntity<User> getUserByUserId(@PathVariable String userId) {
		return new ResponseEntity<>(userService.getUserByUserId(userId), HttpStatus.OK);
	}

}
