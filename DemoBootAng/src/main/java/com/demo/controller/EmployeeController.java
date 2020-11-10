package com.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.model.Employee;
import com.demo.repo.EmpRepository;
import com.demo.service.EmployeeService;
import com.demo.seviceImpl.SequenceGeneratorService;

@RestController

@RequestMapping("/emp/v1/employees")
public class EmployeeController {
	@Autowired
	EmpRepository repo;
	
	@Autowired
	EmployeeService service;
	
	@Autowired
	SequenceGeneratorService sequenceGenerator;
	
	@GetMapping("/{id}")
	public ResponseEntity<Employee> getUser(@PathVariable Long id) {
		
		try{
			Employee e = repo.findById(id).get();
	//	return new ResponseEntity<Employee>(e, HttpStatus.FOUND);
		return ResponseEntity.ok().body(e);


		}catch (Exception e) {
			return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping
	public List<Employee> getAll(){
		
		
		return repo.findAll();				
	}
	
	@PostMapping
	public ResponseEntity<Employee> createEmployee(@RequestBody Employee emp){
		
		
		service.save(emp);
		return new ResponseEntity<Employee>(HttpStatus.CREATED);
		
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<Employee> deleteEmployee(@PathVariable Long id){
		try {
			repo.deleteById(id);
			return new ResponseEntity<Employee>(HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);
		}
	}
	@PutMapping("/{id}")
	public ResponseEntity<Employee> update(@RequestBody Employee employeeDetail, @PathVariable Long id) 
	{
	    try {
	        Employee existEmployee = repo.findById(id).get();
	        existEmployee.setEmailId(employeeDetail.getEmailId());
	        existEmployee.setLastName(employeeDetail.getLastName());
	        existEmployee.setFirstName(employeeDetail.getFirstName());
			final Employee updatedEmployee = repo.save(existEmployee);
	       
	        return new ResponseEntity<Employee>(updatedEmployee, HttpStatus.OK);
	    } catch (NoSuchElementException e) {
	        return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);
	    }

}
}
