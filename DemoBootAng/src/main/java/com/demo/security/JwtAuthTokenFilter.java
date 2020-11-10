package com.demo.security;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

public class JwtAuthTokenFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtTokenUtils jwtTokenUtils;
	
	@Autowired
	  private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		 
		String header = request.getHeader("Authorization");
			 String username = null;
		        String authToken = null;
		        if (header != null && header.startsWith("Bearer ")) {
		            authToken = header.replace("Bearer ","");
		            try {
		                username = jwtTokenUtils.getUsernameFromToken(authToken);
		            } catch (IllegalArgumentException e) {
		                logger.error("an error occured during getting username from token", e);
		            } catch (ExpiredJwtException e) {
		                logger.warn("the token is expired and not valid anymore", e);
		            } catch(SignatureException e){
		                logger.error("Authentication Failed. Username or Password not valid.");
		            }
		        } else {
		            logger.warn("couldn't find bearer string, will ignore the header");
		        }
		        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

		            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		            if (jwtTokenUtils.validateToken(authToken, userDetails)) {
		                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
		                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		                logger.info("authenticated user " + username + ", setting security context");
		                SecurityContextHolder.getContext().setAuthentication(authentication);
		            }
		        }

		        filterChain.doFilter(request, response);
		
	}
		      

}
