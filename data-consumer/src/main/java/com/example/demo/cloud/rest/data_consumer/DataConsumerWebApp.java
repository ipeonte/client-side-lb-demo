package com.example.demo.cloud.rest.data_consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Boot launcher and MVC controller
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class DataConsumerWebApp {

	// Logger
	Logger LOG = LoggerFactory.getLogger(DataConsumerWebApp.class);

	@Value("${server.port:8080}")
	private int port;

	public static void main(String[] args) {
		SpringApplication.run(DataConsumerWebApp.class, args);
	}

	@RequestMapping
	public Integer getPortNumber() throws InterruptedException {
		// Simulate timeout sec
		Thread.sleep((long) (Math.random() * Constants.MAX_READ_TIMEOUT));
		return port;
	}

}
