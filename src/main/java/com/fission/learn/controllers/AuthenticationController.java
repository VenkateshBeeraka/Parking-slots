package com.fission.learn.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fission.learn.DTO.UserDTO;
import com.fission.learn.entity.User;
import com.fission.learn.security.LoginRequest;
import com.fission.learn.service.AuthenticationService;

@RestController
public class AuthenticationController {
	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping(path = RestUri.REGISTER)
	public User saveuser(@Validated @RequestBody UserDTO us) {
		System.out.println("Registration");
		return authenticationService.saveuser(us);
	}

	@PostMapping(path = RestUri.LOGIN)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest logninRequest) throws Exception {
		System.out.println("Login");
		return ResponseEntity.status(HttpStatus.OK).body(authenticationService.getLogIn(logninRequest));
	}
}
