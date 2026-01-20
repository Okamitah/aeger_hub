package com.example;

import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AegerHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(AegerHubApplication.class, args);
    }

    // This runs automatically on startup
    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            // Check if admin exists, if not, create it
            if (repository.findByUsername("admin").isEmpty()) {
                System.out.println("--- DATABASE TEST: Creating default user 'admin' ---");
                repository.save(new UserEntity("admin", "secret"));
                System.out.println("--- DATABASE TEST: User 'admin' saved successfully! ---");
            }
        };
    }
}