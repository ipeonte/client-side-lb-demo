package com.example.demo.cloud.rest.data_provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Data Provider configuration class
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
@Configuration
public class DataProviderConfig {

	@Value("${demo.read_timeout:1000}")
	private int readTimeout;

	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
		rf.setReadTimeout(readTimeout);

		return new RestTemplate(rf);
	}

}
