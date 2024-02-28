package com.hcl.jtl.authserver.security.config;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.hcl.jtl.authserver.exception.InvalidTokenException;
import com.hcl.jtl.authserver.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTHelper {
	
	Logger logger = LoggerFactory.getLogger(JWTHelper.class);

	private final static String SECRET_KEY = "secret";
	
	@Autowired
	UserRepository userRepository;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY.getBytes(Charset.forName("UTF-8"))).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userDetails.getUsername());
	}

	private String createToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()).compact();
	}

//	public Boolean validateToken(String token, UserDetails userDetails) {
//		final String username = extractUsername(token);
//		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	}
	
	public Boolean validateToken(String token) throws InvalidTokenException{
		final String username = extractUsername(token);
		logger.info("username -->"+username);
		logger.info("userRepository.findUserByEmail(username).getEmail() -->"+userRepository.findUserByEmail(username).getEmail());
		logger.info("userRepository.findUserByEmail(username).getUserRoles() -->"+userRepository.findUserByEmail(username).getUserRoles());
		if(username.equals(userRepository.findUserByEmail(username).getEmail()) && userRepository.findUserByEmail(username).getUserRoles().contains("ADMIN") && !isTokenExpired(token))
			return true;
		else
			return false;
	}

}