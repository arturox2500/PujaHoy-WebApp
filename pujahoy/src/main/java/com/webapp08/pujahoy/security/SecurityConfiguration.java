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
	
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/h2-console").permitAll()
				// PUBLIC PAGES
				.requestMatchers("/").permitAll()
				.requestMatchers("/css/**").permitAll()
				.requestMatchers("/img/**").permitAll()
				.requestMatchers("/js/**").permitAll()
				.requestMatchers("/static/**").permitAll()
				.requestMatchers("/producto/{id_producto}").permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/register").permitAll()
				.requestMatchers("/usuario/{id}").permitAll()
				.requestMatchers("/producto/{id}/image").permitAll()
				.requestMatchers("/usuario/producto_template").permitAll()
				.requestMatchers("/producto_template").permitAll()
				.requestMatchers("/producto_template_index").permitAll()
				// PRIVATE PAGES
				.requestMatchers("/product/*/delete").hasAnyRole("ADMIN")
				.requestMatchers("/product/{id_producto}/place-bid").hasAnyRole("USER")
				.requestMatchers("/usuario").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/usuario/{id}/banear").hasAnyRole("ADMIN")
				.requestMatchers("/usuario/{id}/rate").hasAnyRole("USER")
				.requestMatchers("/usuario/producto_template_compras").hasAnyRole("USER")
				.requestMatchers("/usuario/submit_auction").hasAnyRole("USER")
				.requestMatchers("/usuario/NuevoProducto").hasAnyRole("USER")
				.requestMatchers("/usuario/verCompras").hasAnyRole("USER")
				.requestMatchers("/usuario/verProductos").hasAnyRole("USER")
				.requestMatchers("/usuario/{id}/rated").hasAnyRole("USER")
			)
			.formLogin(formLogin -> formLogin
					.loginPage("/login")					
					.failureUrl("/loginerror")
					.defaultSuccessUrl("/")
					.permitAll()
			)
			.rememberMe(rememberMe -> rememberMe
				.key("uniqueAndSecret")
				.tokenValiditySeconds(86400) // 1 día de sesión activa
			)
			.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.permitAll()
			);
		
		// Disable CSRF at the moment
		http.csrf(csrf -> csrf.disable());
		http.headers().frameOptions().sameOrigin();

		return http.build();
	}

}