package com.demo.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.model.AuthToken;
import com.demo.model.ERole;
import com.demo.model.Employee;
import com.demo.model.LoginUser;
import com.demo.model.Role;
import com.demo.model.SignUpRequest;
import com.demo.repo.EmpRepository;
import com.demo.repo.RoleRepository;
import com.demo.security.JwtTokenUtils;
import com.demo.service.EmployeeService;
import com.demo.seviceImpl.SequenceGeneratorService;


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
	    private RoleRepository roleRepository;
	    
	    @Autowired
		private SequenceGeneratorService sequenceGenerator;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginUser loginUser) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        Employee e=repo.getByUsername(loginUser.getUsername());
        Set<Role> s = e.getRoles();
        Set role = s.stream().map(p->p.getName()).collect(Collectors.toSet());
        System.out.println(role);

        return ResponseEntity.ok(new AuthToken(token, loginUser.getUsername(), role));
    //    return ResponseEntity.ok(new AuthToken(token, loginUser.getUsername()));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest){
		
		if(repo.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().build();
		}
		if(repo.existsByEmailId(signUpRequest.getEmailId())) {
			return ResponseEntity.badRequest().build();
		}
		
		Employee emp = new Employee();
		
		
		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
			
			
	}
		emp.setId(sequenceGenerator.generateSequence(signUpRequest.SEQUENCE_NAME));
		emp.setUsername(signUpRequest.getUsername());
		emp.setEmailId(signUpRequest.getEmailId());
		emp.setFirstName(signUpRequest.getFirstName());
		emp.setLastName(signUpRequest.getLastName());
		emp.setPassword(signUpRequest.getPassword());
		emp.setRoles(roles);
		userService.save(emp);
		return ResponseEntity.ok("User created");
	}
	}
