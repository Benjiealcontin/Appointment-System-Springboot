package com.appointment.CancelService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CancelServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CancelServiceApplication.class, args);
	}

}
