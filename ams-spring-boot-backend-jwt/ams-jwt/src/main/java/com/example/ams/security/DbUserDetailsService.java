package com.example.ams.security;

import com.example.ams.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
  private final UserRepo users;
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var u = users.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return new User(u.getEmail(), u.getPasswordHash(), List.of(new SimpleGrantedAuthority("ROLE_"+u.getRole())));
  }
}
