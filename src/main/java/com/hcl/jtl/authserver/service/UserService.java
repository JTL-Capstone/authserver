package com.hcl.jtl.authserver.service;

import java.util.List;

import com.hcl.jtl.authserver.entity.User;
import com.hcl.jtl.authserver.exception.ObjectAlreadyExistsException;
import com.hcl.jtl.authserver.exception.ObjectNotExistsException;

public interface UserService {

	List<User> getAllUsers();
	
	User createUser(User user) throws ObjectAlreadyExistsException;
	
	void deleteUserByUserId(String userId) throws ObjectNotExistsException;
	
	public User getUserByUserId(String userId) ;
	
	User findUserByUserName(String userName);
	
}
