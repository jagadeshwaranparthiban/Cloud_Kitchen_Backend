package com.cloudkitchenbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CloudKitchenBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudKitchenBackendApplication.class, args);
	}

}
