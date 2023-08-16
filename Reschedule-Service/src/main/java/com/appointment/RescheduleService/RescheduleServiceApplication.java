package com.appointment.RescheduleService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RescheduleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RescheduleServiceApplication.class, args);
	}

}
