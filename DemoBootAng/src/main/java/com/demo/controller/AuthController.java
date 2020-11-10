package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.model.AuthToken;
import com.demo.model.Employee;
import com.demo.model.LoginUser;
import com.demo.model.MessageResponse;
import com.demo.repo.EmpRepository;
import com.demo.security.JwtTokenUtils;
import com.demo.service.EmployeeService;
import com.demo.seviceImpl.SequenceGeneratorService;

import ch.qos.logback.classic.pattern.MessageConverter;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/emp/v1/auth")
public class AuthController {
	  @Autowired
	    private AuthenticationManager authenticationManager;

	    @Autowired
	    private JwtTokenUtils jwtTokenUtil;

	    @Autowired
	    private EmployeeService userService;
	    
	    @Autowired
	    private EmpRepository repo;
	    
	    @Autowired
		private SequenceGeneratorService sequenceGenerator;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginUser loginUser) throws AuthenticationException{
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
		final Employee user = userService.findOne(loginUser.getUsername());
        final String token = jwtTokenUtil.generateToken(user);
        return ResponseEntity.ok(new AuthToken(token, user.getUsername()));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody Employee emp){
		if(repo.existsByUsername(emp.getUsername())) {
			return ResponseEntity.badRequest().build();
		}
		if(repo.existsByEmailId(emp.getEmailId())) {
			return ResponseEntity.badRequest().build();
		}
		emp.setId(sequenceGenerator.generateSequence(emp.SEQUENCE_NAME));
		userService.save(emp);
		return ResponseEntity.ok("User created");
	}
	

}
