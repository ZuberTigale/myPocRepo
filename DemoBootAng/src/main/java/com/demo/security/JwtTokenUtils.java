package com.demo.security;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.demo.model.Employee;
import com.mongodb.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtils {
	
	@Value("${com.demo.jwtSecret}")
	private String jwtSecret;

	@Value("${com.demo.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String getUsernameFromToken(String token) {
	        return getClaimFromToken(token, Claims::getSubject);
	    }

	    public Date getExpirationDateFromToken(String token) {
	        return getClaimFromToken(token, Claims::getExpiration);
	    }

	    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = getAllClaimsFromToken(token);
	        return claimsResolver.apply(claims);
	    }

	    private Claims getAllClaimsFromToken(String token) {
	        return Jwts.parser()
	                .setSigningKey(jwtSecret)
	                .parseClaimsJws(token)
	                .getBody();
	    }

	    private Boolean isTokenExpired(String token) {
	        final Date expiration = getExpirationDateFromToken(token);
	        return expiration.before(new Date());
	    }
	    
	    public String generateToken(Employee user) {
	        return doGenerateToken(user.getUsername());
	    }
	    
	    private String doGenerateToken(String subject) {

	        Claims claims = Jwts.claims().setSubject(subject);
	        claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

	        return Jwts.builder()
	                .setClaims(claims)
	                .setIssuer("http://tigale.com")
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs*1000))
	                .signWith(SignatureAlgorithm.HS256, jwtSecret)
	                .compact();
	    }

	    public Boolean validateToken(String token, UserDetails userDetails) {
	        final String username = getUsernameFromToken(token);
	        return (
	              username.equals(userDetails.getUsername())
	                    && !isTokenExpired(token));
	    }
	

}
