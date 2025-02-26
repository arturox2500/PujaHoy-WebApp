package com.webapp08.pujahoy.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import java.util.Optional;
import org.springframework.security.core.Authentication;


import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.repository.UsuarioRepository;
import com.webapp08.pujahoy.service.RepositoryUserDetailsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



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
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new SimpleUrlAuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
												Authentication authentication) throws IOException, ServletException {
				HttpSession session = request.getSession();
				String username = authentication.getName();
				
				System.out.println("Usuario autenticado: " + username); // LOG para verificar el usuario
				
				// Buscar el usuario en la base de datos
				Optional<Usuario> user = usuarioRepository.findByNombre(username);
				
				if (user.isPresent()) {
					session.setAttribute("id", user.get().getId());
					System.out.println("ID guardado en sesión: " + user.get().getId()); // LOG para verificar el ID
				} else {
					System.out.println("No se encontró el usuario en la base de datos.");
				}

				super.onAuthenticationSuccess(request, response, authentication);
			}
		};
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
					.successHandler(authenticationSuccessHandler())
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