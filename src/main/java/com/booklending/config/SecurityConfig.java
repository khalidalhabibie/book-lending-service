package com.booklending.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.POST, "/api/members/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/api/members/**").hasRole("ADMIN")
        .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    UserDetails admin =
        User.withUsername("admin").password("{noop}admin123").roles("ADMIN").build();

    UserDetails user = User.withUsername("user").password("{noop}user123").roles("USER").build();

    return new InMemoryUserDetailsManager(admin, user);
  }
}
