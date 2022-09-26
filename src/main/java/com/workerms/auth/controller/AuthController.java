package com.workerms.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

import com.workerms.auth.data.vo.UserVo;
import com.workerms.auth.jwt.JwtTokenProvider;
import com.workerms.auth.repository.UserRepository;

@RestController
@RequestMapping("/login")
public class AuthController {
	
	public final AuthenticationManager authenticationManager;
	public final JwtTokenProvider jwtTokenProvider;
	public final UserRepository userRepository;
	
	@Autowired
	public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
			UserRepository userRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userRepository = userRepository;
	}
	
	
	@RequestMapping("/testSecurity")
	public String teste() {
		return "ok";
	}
	
	@PostMapping(produces = { "application/json", "application/xml", "application/x-yaml" }, consumes = {
			"application/json", "application/xml", "application/x-yaml" })
	public ResponseEntity<?> login(@RequestBody UserVo userVo) {
		try {
			var username = userVo.getUserName();
			var password = userVo.getPassword();
			
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			var user = userRepository.findByUserName(username);
			var token = "";
			
			if(user != null) {
				token = jwtTokenProvider.createToken(username, user.getRoles());
			}
			else {
				throw new UsernameNotFoundException("username not found");
			}
			
			Map<Object, Object> model = new HashMap<>();
			model.put("Username", username);
			model.put("Token", token);
			ok(model);
		} catch (AuthenticationException e) {
			throw new BadCredentialsException("Invalid username/password");
		}
		return null;
	}	

}
