package com.fission.learn.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fission.learn.entity.User;
import com.fission.learn.exceptions.ResourceNotFoundException;
import com.fission.learn.repository.AuthenticationRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private AuthenticationRepository authenticationRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws ResourceNotFoundException {
		System.out.println("MyUserDetails");

		User user = authenticationRepository.findByemail(email);

		if (user == null) {
			throw new ResourceNotFoundException("user not found");
		}

		return new LoginPrincipal(user);

	}
}
