package com.fission.learn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fission.learn.entity.User;

public interface AuthenticationRepository extends JpaRepository<User, Integer> {

	User findByemail(String email);
}
