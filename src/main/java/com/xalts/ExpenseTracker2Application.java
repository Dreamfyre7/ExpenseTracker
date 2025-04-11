package com.xalts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class ExpenseTracker2Application {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseTracker2Application.class, args);
	}

}
