package de.opentiming.feigWS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class FeigWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeigWsApplication.class, args);
	}
}
