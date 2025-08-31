package com.example.ams.seed;

import com.example.ams.model.User;
import com.example.ams.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedRunner {
  @Bean
  CommandLineRunner seedUsers(UserRepo users, PasswordEncoder encoder) {
    return args -> {
      if (users.count() == 0) {
        User approver = new User();
        approver.setEmail("approver@ams.local");
        approver.setPasswordHash(encoder.encode("demo"));
        approver.setFullName("Demo Approver");
        approver.setRole("APPROVER");
        users.save(approver);

        User employee = new User();
        employee.setEmail("employee@ams.local");
        employee.setPasswordHash(encoder.encode("demo"));
        employee.setFullName("Demo Employee");
        employee.setRole("EMPLOYEE");
        users.save(employee);
      }
    };
  }
}
