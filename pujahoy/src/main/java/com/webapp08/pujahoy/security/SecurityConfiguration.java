package com.webapp08.pujahoy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.webapp08.pujahoy.repository.UsuarioRepository;
import com.webapp08.pujahoy.service.RepositoryUserDetailsService;



@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
    public RepositoryUserDetailsService userDetailService;

	@Autowired
    public UsuarioRepository usuarioRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.authorizeHttpRequests(authorize -> authorize
				.anyRequest().permitAll()
					// PUBLIC PAGES
					//.requestMatchers("/").permitAll()
					//.requestMatchers("/css/**").permitAll()
                	//.requestMatchers("/img/**").permitAll()
                	//.requestMatchers("/static/**").permitAll()
					//.requestMatchers("/vendedor/**").permitAll()
					//.requestMatchers("/login/**").permitAll()
					// PRIVATE PAGES
					//.requestMatchers("/usuario").hasAnyAuthority("USER", "ADMIN")
					//.requestMatchers("/usuario/*").hasAnyAuthority("USER", "ADMIN")

			)
			.formLogin(formLogin -> formLogin
					.loginPage("/login")
					.failureUrl("/loginerror")
					.defaultSuccessUrl("/")
					.permitAll()
			)
			.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.permitAll()
			);
		
		// Disable CSRF at the moment
		http.csrf(csrf -> csrf.disable());

		return http.build();
	}

}
