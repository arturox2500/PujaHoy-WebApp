
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
import com.webapp08.pujahoy.repository.UserModelRepository;
import com.webapp08.pujahoy.service.RepositoryUserDetailsService;



@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
    public RepositoryUserDetailsService userDetailService;

	@Autowired
    public UserModelRepository usuarioRepository;

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
				// PUBLIC PAGES
				.requestMatchers("/").permitAll()
				.requestMatchers("/css/**").permitAll()
				.requestMatchers("/img/**").permitAll()
				.requestMatchers("/js/**").permitAll()
				.requestMatchers("/static/**").permitAll()
				.requestMatchers("/product/{id_product}").permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/logout").permitAll()
				.requestMatchers("/loginerror").permitAll()
				.requestMatchers("/register").permitAll()
				.requestMatchers("/user/{id}").permitAll()
				.requestMatchers("/product/{id}/image").permitAll()
				.requestMatchers("/user/product_template").permitAll()
				.requestMatchers("/product_template").permitAll()
				.requestMatchers("/product_template_index").permitAll()
				.requestMatchers("/user/profile-picture/**").permitAll()
				.requestMatchers("/user/{id}/profilePic").permitAll()
				.requestMatchers("/product/*").permitAll()
				.requestMatchers("/permitsError").permitAll()
				// PRIVATE PAGES
				.requestMatchers("/product/*/delete").hasAnyRole("ADMIN")
				.requestMatchers("/product/{id_product}/place-bid").hasAnyRole("USER")
				.requestMatchers("/user").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/user/{id}/ban").hasAnyRole("ADMIN")
				.requestMatchers("/user/{id}/rate").hasAnyRole("USER")
				.requestMatchers("/user/product_template_buys").hasAnyRole("USER")
				.requestMatchers("/user/submit_auction").hasAnyRole("USER")
				.requestMatchers("/user/newProduct").hasAnyRole("USER")
				.requestMatchers("/user/seeBuys").hasAnyRole("USER")
				.requestMatchers("/user/seeProducts").hasAnyRole("USER")
				.requestMatchers("/user/{id}/rated").hasAnyRole("USER")
				.requestMatchers("/product/{id_product}/finish").hasAnyRole("USER")
			)
			.formLogin(formLogin -> formLogin
				.loginPage("/login")					
				.failureUrl("/loginerror")
				.defaultSuccessUrl("/")
				.permitAll()
			)
			.rememberMe(rememberMe -> rememberMe
				.key("uniqueAndSecret")
				.tokenValiditySeconds(86400) // 1 day active session
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.permitAll()
			)
			.exceptionHandling(exceptionHandling -> exceptionHandling
            	.accessDeniedPage("/permitsError") // Redirect to /pageError if have error 403
        	);
			
		http.headers().frameOptions().sameOrigin();

		return http.build();
	}

}