package com.hcl.jtl.authserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcl.jtl.authserver.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	void deleteUserByUserId(String userId);

	Optional<User> findUserByUserId(String userId);
	
	User findUserByUserName(String userName);

	User findUserByEmail(String email);

}
