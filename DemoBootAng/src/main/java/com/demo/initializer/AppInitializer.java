package com.demo.initializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.demo")
@EnableJpaRepositories("com.demo.repo")
@EntityScan("com.demo.model")
public class AppInitializer {
	public static void main(String[] args) {
		SpringApplication.run(AppInitializer.class, args);
	}
}
