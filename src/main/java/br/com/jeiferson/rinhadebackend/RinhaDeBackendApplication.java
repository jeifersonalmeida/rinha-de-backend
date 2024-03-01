package br.com.jeiferson.rinhadebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RinhaDeBackendApplication {

	static {
		System.out.println("Logging tuning properties...");
		System.out.println("ENABLE_POOL: " + System.getenv("ENABLE_POOL"));
		System.out.println("INITIAL_POOL_SIZE: " + System.getenv("INITIAL_POOL_SIZE"));
		System.out.println("MAX_POOL_SIZE: " + System.getenv("MAX_POOL_SIZE"));
	}

	public static void main(String[] args) {
		SpringApplication.run(RinhaDeBackendApplication.class, args);
	}

}
