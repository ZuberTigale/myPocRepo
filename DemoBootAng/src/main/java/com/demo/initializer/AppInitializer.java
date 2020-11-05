package com.demo.initializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.demo")
@EnableMongoRepositories("com.demo.repo")
@EntityScan("com.demo.model")
public class AppInitializer {
	public static void main(String[] args) {
		SpringApplication.run(AppInitializer.class, args);
	}
}
