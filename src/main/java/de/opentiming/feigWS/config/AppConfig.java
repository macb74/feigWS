package de.opentiming.feigWS.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.opentiming.feigWS.reader.FedmConnect;


@Configuration
public class AppConfig {
	
    @Bean
    public Map<String, FedmConnect> connections() {
    	Map<String, FedmConnect> connections = new HashMap<>();
		return connections;
    }
}