package com.demo.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.model.Employee;
import com.demo.model.SignUpRequest;
import com.demo.repo.EmpRepository;

@Service
public class EmployeeService implements UserDetailsService {
	
	@Autowired
	EmpRepository repository;
	
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Employee user=repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
	}
	private Set getAuthority(Employee user) {
        Set authorities = new HashSet();
		user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
		});
		return authorities;
	}

	
	public  Employee save(Employee signUp) {
		Employee newUser=new Employee();
		
		newUser.setId(signUp.getId());
		newUser.setUsername(signUp.getUsername());
		newUser.setFirstName(signUp.getFirstName());
		newUser.setLastName(signUp.getLastName());
		newUser.setEmailId(signUp.getEmailId());
		newUser.setPassword(bcryptEncoder.encode(signUp.getPassword()));
		newUser.setRoles(signUp.getRoles());
		repository.save(newUser);
		
		return null;
	}
	
	/*
	 * public Employee findOne(String username) { return
	 * repository.findByUsername(username); }
	 */
	

}
