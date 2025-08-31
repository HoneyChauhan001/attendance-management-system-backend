package com.example.ams.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity @Table(name = "users")
@Getter @Setter
public class User {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String email;

  @JsonIgnore
  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false)
  private String role; // EMPLOYEE/APPROVER/ADMIN
}
