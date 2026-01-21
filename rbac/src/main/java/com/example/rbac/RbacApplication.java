package com.example.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.rbac.config.EnvInitializer;

@SpringBootApplication
public class RbacApplication {

	public static void main(String[] args) {
		//SpringApplication.run(RbacApplication.class, args);
		SpringApplication app = new SpringApplication(RbacApplication.class);
		app.addInitializers(new EnvInitializer());
		app.run(args);
	}

}
