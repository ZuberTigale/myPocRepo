package com.demo.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.model.ERole;
import com.demo.model.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
	Optional<Role> findByName(ERole name);

}
