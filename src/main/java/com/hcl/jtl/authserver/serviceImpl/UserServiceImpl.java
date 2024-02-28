package com.hcl.jtl.authserver.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.core.userdetails.User.UserBuilder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hcl.jtl.authserver.entity.User;
import com.hcl.jtl.authserver.exception.ObjectAlreadyExistsException;
import com.hcl.jtl.authserver.exception.ObjectNotExistsException;
import com.hcl.jtl.authserver.repository.UserRepository;
import com.hcl.jtl.authserver.service.UserService;
import com.hcl.jtl.authserver.util.Constants;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService{
	
	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserRepository userRepository;

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User createUser(User user) throws ObjectAlreadyExistsException {
		Optional<User> optUser = userRepository.findUserByUserId(user.getUserId());
		if (!optUser.isPresent()) {
			return userRepository.save(user);
		}else {
			logger.error(user.getUserId()+" "+Constants.RECORD_ALREADY_EXISTS);
			throw new ObjectAlreadyExistsException(Constants.RECORD_ALREADY_EXISTS);
		}
	}
	
	@Override
	@Transactional
	public void deleteUserByUserId(String userId) throws ObjectNotExistsException{
		Optional<User> optUser = userRepository.findUserByUserId(userId);
		if (optUser.isPresent()) {
			userRepository.deleteUserByUserId(userId);
			logger.info(userId+" UserId is deleted");
		}else {
			logger.error(userId+" "+Constants.RECORD_NOT_EXISTS);;
			throw new ObjectNotExistsException(userId+" "+Constants.RECORD_NOT_EXISTS);
		}
	}
	
	
	@Override
	public User getUserByUserId(String userId) {
		return userRepository.findUserByUserId(userId).get();
	}

	@Override
	public User findUserByUserName(String userName) {
		return userRepository.findUserByUserName(userName);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findUserByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("User not Found");
        }
        UserBuilder builder = null;
        builder = org.springframework.security.core.userdetails.User.withUsername(email);
		builder.password(new BCryptPasswordEncoder().encode(user.getPassword()));
		System.out.println("ROLE===="+user.getUserRoles().iterator().next().toString());
		//builder.roles(user.getRoles().iterator().next().getName().toString(),"ADMIN");
		builder.roles(user.getUserRoles().iterator().next().toString());
        return builder.build();
	}

}
