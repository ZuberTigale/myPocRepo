package com.demo.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.model.Employee;
import com.demo.repo.EmpRepository;

@Service
public class EmployeeService implements UserDetailsService {
	
	@Autowired
	EmpRepository repository;
	
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Employee user=repository.findByUsername(username);
		if(user == null){
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority());
	}
	
	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
	
	public  Employee save(Employee e) {
		Employee newUser=new Employee();
		
		newUser.setId(e.getId());
		newUser.setFirstName(e.getFirstName());
		newUser.setLastName(e.getLastName());
		newUser.setEmailId(e.getEmailId());
		newUser.setPassword(bcryptEncoder.encode(e.getPassword()));
		repository.save(newUser);
		
		return null;
	}
	
	public Employee findOne(String username) {
		return repository.findByUsername(username);
	}
	

}
