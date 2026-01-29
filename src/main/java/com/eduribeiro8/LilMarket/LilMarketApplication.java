package com.eduribeiro8.LilMarket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LilMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(LilMarketApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(){
		return  runner -> {};
	}

}
