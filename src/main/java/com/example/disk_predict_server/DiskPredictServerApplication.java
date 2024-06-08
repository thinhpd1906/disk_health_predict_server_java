package com.example.disk_predict_server;

import com.example.disk_predict_server.api.dto.request.RegisterRequest;
import com.example.disk_predict_server.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
//@CrossOrigin(origins = "*")
public class DiskPredictServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(DiskPredictServerApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {
			var admin = RegisterRequest.builder()
					.id("ad123456")
					.fullName("Admin")
					.email("admin@gmail.com")
					.password("Thinh123")
					.role("ADMIN")
					.build();
			System.out.println("Admin: " + service.register(admin));

			var user = RegisterRequest.builder()
					.id("LANRCX04K973449")
					.fullName("User")
					.email("phanthinhpdt@gmail.com")
					.password("Thinh123")
					.role("USER")
					.build();
			System.out.println("USER: " + service.register(user));

		};
	}
}
