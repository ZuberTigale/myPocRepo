package com.demo.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.demo.model.Employee;
import com.mongodb.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
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
	    
	    public String generateToken(Authentication authentication) {
	    	 final String authorities = authentication.getAuthorities().stream()
	                 .map(GrantedAuthority::getAuthority)
	                 .collect(Collectors.joining(","));
	         return Jwts.builder()
	                 .setSubject(authentication.getName())
	                 .claim("scopes", authorities)
	                 .setIssuer("http://tigale.com")
	                 .signWith(SignatureAlgorithm.HS256, jwtSecret)
	                 .setIssuedAt(new Date(System.currentTimeMillis()))
	                 .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs*1000))
	                 .compact();
	    }
	    
	/*
	 * private String doGenerateToken(String subject) {
	 * 
	 * Claims claims = Jwts.claims().setSubject(subject); final String authorities =
	 * authentication.getAuthorities().stream() .map(GrantedAuthority::getAuthority)
	 * .collect(Collectors.joining(","));
	 * 
	 * return Jwts.builder() .setClaims(claims) .setIssuer("http://tigale.com")
	 * .setIssuedAt(new Date(System.currentTimeMillis())) .setExpiration(new
	 * Date(System.currentTimeMillis() + jwtExpirationMs*1000))
	 * .signWith(SignatureAlgorithm.HS256, jwtSecret) .compact(); }
	 */
	    
	    UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails) {

	        final JwtParser jwtParser = Jwts.parser().setSigningKey(jwtSecret);

	        final Jws claimsJws = jwtParser.parseClaimsJws(token);

	        final Claims claims = (Claims) claimsJws.getBody();

	        final Collection<? extends GrantedAuthority> authorities =
	                Arrays.stream(claims.get("scopes").toString().split(","))
	                        .map(SimpleGrantedAuthority::new)
	                        .collect(Collectors.toList());

	        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
	    }

	    public Boolean validateToken(String token, UserDetails userDetails) {
	        final String username = getUsernameFromToken(token);
	        return (
	              username.equals(userDetails.getUsername())
	                    && !isTokenExpired(token));
	    }
	

}
