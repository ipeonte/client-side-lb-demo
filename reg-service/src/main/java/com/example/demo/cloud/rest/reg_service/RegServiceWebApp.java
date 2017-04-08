package com.example.demo.cloud.rest.reg_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class RegServiceWebApp {

	public static void main(String[] args) {
		SpringApplication.run(RegServiceWebApp.class, args);
	}
}
