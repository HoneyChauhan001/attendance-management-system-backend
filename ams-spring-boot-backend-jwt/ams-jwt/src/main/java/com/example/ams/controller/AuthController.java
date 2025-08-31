package com.example.ams.controller;

import com.example.ams.model.User;
import com.example.ams.repo.UserRepo;
import com.example.ams.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtService jwtService;
  private final UserRepo users;
  private final PasswordEncoder encoder;

  public record LoginReq(String username, String password) {}

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody LoginReq req) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
    } catch (BadCredentialsException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    UserDetails userDetails = userDetailsService.loadUserByUsername(req.username());
    String token = jwtService.generateToken(userDetails);
    User u = users.findByEmail(req.username()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    return Map.of("accessToken", token, "user", Map.of("id", u.getId(), "email", u.getEmail(), "role", u.getRole(), "fullName", u.getFullName()));
  }
}
