package com.example;

import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import com.example.service.MealService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AegerHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(AegerHubApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, MealService mealService) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                System.out.println("--- DATABASE: Creating default user 'admin' ---");
                userRepository.save(new UserEntity("admin", "secret"));
                System.out.println("--- DATABASE: User 'admin' created! ---");
            }
            
            System.out.println("--- DATABASE: Initializing meal recommendations ---");
            mealService.initializeMealDatabase();
            System.out.println("--- DATABASE: Meal database initialized! ---");
        };
    }
}
