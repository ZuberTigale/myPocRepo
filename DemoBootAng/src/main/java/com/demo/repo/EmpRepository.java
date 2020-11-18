package com.demo.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.Employee;

@Repository
public interface EmpRepository extends MongoRepository<Employee, Long> {
	
	Optional<Employee> findByUsername(String username);

	  Boolean existsByUsername(String username);

	  Boolean existsByEmailId(String emailId);
	

}
