package com.hcl.jtl.authserver.security.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JWTHelper jwtHelper;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// get header
		// Start with Bearer
		// validate
		String reqTokenHeader = request.getHeader("Authorization");
		String userName = null;

		if (reqTokenHeader != null && reqTokenHeader.startsWith("Bearer ")) {
			String tokenWithoutBearer = reqTokenHeader.substring(7);

			try {
				userName = this.jwtHelper.extractUsername(tokenWithoutBearer);
			} catch (Exception e) {
				e.printStackTrace();
				// throw new Exception("Invalid token!!");
			}

			UserDetails user = this.userDetailsService.loadUserByUsername(userName);
			if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						user, null, user.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			} else {
				// any message
				System.out.println("Token not validated!!");
			}
		}

		filterChain.doFilter(request, response);

	}

}
