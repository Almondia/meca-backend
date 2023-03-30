package com.almondia.meca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = {"com.almondia.*"})
public class MecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MecaApplication.class, args);
	}
}
