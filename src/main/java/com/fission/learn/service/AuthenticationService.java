package com.fission.learn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fission.learn.DTO.UserDTO;
import com.fission.learn.entity.User;
import com.fission.learn.repository.AuthenticationRepository;
import com.fission.learn.repository.SlotBookingRepository;
import com.fission.learn.security.AdminLogin;
import com.fission.learn.security.JwtUtil;
import com.fission.learn.security.LoginPrincipal;
import com.fission.learn.security.LoginRequest;
import com.fission.learn.security.UserLogin;

@Service
public class AuthenticationService {

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private SlotBookingRepository slotBookingRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AuthenticationRepository authenticationRepository;

	public User saveuser(UserDTO dto) {
		User user = new User();
		user.setId(dto.getId());
		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());
		user.setCity(dto.getCity());
		user.setRole(dto.getRole());
		user.setBuildings(dto.getBuildings());
		return authenticationRepository.save(user);
	}

	public Object getLogIn(LoginRequest loginRequest) {

		Authentication auth = null;
		auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		LoginPrincipal princple = (LoginPrincipal) auth.getPrincipal();

		User myuser = princple.getUser();
		Integer id = myuser.getId();
		String role = myuser.getRole();

		final String jwt = jwtUtil.generateToken(princple);

		if (role.equals("admin")) {
			User user = authenticationRepository.findById(id).orElse(null);
			return new AdminLogin(jwt, user.getBuildings());
		} else {
			return new UserLogin(jwt, slotBookingRepository.endUserResult(id));

		}
	}
}
