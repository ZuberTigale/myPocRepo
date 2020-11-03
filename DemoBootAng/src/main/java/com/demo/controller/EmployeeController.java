package com.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/emp/v1")
public class EmployeeController {
	@Autowired
	EmpRepository repo;
	
	@GetMapping("/get/{id}")
	public ResponseEntity<Employee> getUser(@PathVariable Long id) {
		System.out.println("checkget");
		try{
			Employee e = repo.findById(id).get();
		return new ResponseEntity<Employee>(e, HttpStatus.FOUND);
		}catch (Exception e) {
			return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping("/getall")
	public List<Employee> getAll(){
		System.out.println("checkgetall");
		
		return repo.findAll();				
	}
	
	@PostMapping("/createemployee")
	public ResponseEntity<Employee> createEmployee(@RequestBody Employee emp){
		System.out.println("check");
		repo.save(emp);
		return new ResponseEntity<Employee>(HttpStatus.CREATED);
	}
	@DeleteMapping("/deleteemployee/{id}")
	public ResponseEntity<Employee> deleteEmployee(@PathVariable Long id){
		try {
			repo.deleteById(id);
			return new ResponseEntity<Employee>(HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);
		}
	}
	@PutMapping("/updateemployee/{id}")
	public ResponseEntity<Employee> update(@RequestBody Employee emp, @PathVariable Long id) 
	{
	    try {
	        Employee existEmployee = repo.findById(id).get();
	        repo.save(emp);
	        return new ResponseEntity<Employee>(HttpStatus.OK);
	    } catch (NoSuchElementException e) {
	        return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);
	    }

}
}
