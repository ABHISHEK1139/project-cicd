package com.carrentalsystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.carrentalsystem.filter.JwtAuthFilter;
import com.carrentalsystem.utility.Constants.UserRole;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthFilter authFilter;

	@Bean
	// authentication
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable())

				// Public endpoints: login, customer self-registration, and read-only
				// browsing (variant/company fetches and stored-image downloads)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/user/login", "/api/user/register").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/variant/fetch/**", "/api/variant/search",
								"/api/company/fetch/**", "/api/variant/*", "/api/user/*")
						.permitAll()

						// Admin self-registration and user management require an existing
						// admin's JWT (anonymous callers get 403)
						.requestMatchers("/api/user/admin/register", "/api/user/fetch/role-wise",
								"/api/user/update/status", "/api/user/delete/**")
						.hasAuthority(UserRole.ROLE_ADMIN.value())

						// NOTE: the remaining APIs stay open for now. Requiring
						// authentication for them needs the frontend to send the JWT on
						// every request (currently every Authorization header is
						// commented out), so that change is deliberately out of scope
						// here to avoid breaking the working UI.
						.anyRequest().permitAll())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
