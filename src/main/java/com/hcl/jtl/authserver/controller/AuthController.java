package com.hcl.jtl.authserver.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.jtl.authserver.dto.JwtRequest;
import com.hcl.jtl.authserver.dto.JwtResponse;
import com.hcl.jtl.authserver.exception.InvalidTokenException;
import com.hcl.jtl.authserver.security.config.JWTHelper;


@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTHelper jwtHelper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
	public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest){
		this.doAuthenticate(jwtRequest.getEmail(), jwtRequest.getPassword());
		UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getEmail());
		String token = this.jwtHelper.generateToken(userDetails);
		
		JwtResponse jwtResponse = new JwtResponse();
		jwtResponse.setToken(token);
		jwtResponse.setUserName(userDetails.getUsername());
		return new ResponseEntity<>(jwtResponse, org.springframework.http.HttpStatus.OK);
	}
    
    @PostMapping("/validate")
	public ResponseEntity<Boolean> validate(@RequestBody Map<String, String> requestBody) throws InvalidTokenException {
	    String token = requestBody.get("token");
	    return new ResponseEntity<>(jwtHelper.validateToken(token), org.springframework.http.HttpStatus.OK);
	}
	
	private void doAuthenticate(String email, String password) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, password);
		try {
			authenticationManager.authenticate(usernamePasswordAuthenticationToken);
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("Invalid UserName or Password !!");
		}
	}
}
