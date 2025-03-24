
package com.webapp08.pujahoy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.webapp08.pujahoy.security.jwt.JwtRequestFilter;
import com.webapp08.pujahoy.security.jwt.UnauthorizedHandlerJwt;
import com.webapp08.pujahoy.service.RepositoryUserDetailsService;



@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	RepositoryUserDetailsService userDetailsService;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.securityMatcher("/api/**")
			.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
		
		http
			.authorizeHttpRequests(authorize -> authorize
                    // PUBLIC ENDPOINTS
                    .requestMatchers(HttpMethod.GET,"/api/v1/users/*").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/v1/users/*/image").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/v1/products/*").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/v1/products").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/v1/products/*/image").permitAll()
					.requestMatchers(HttpMethod.GET,"/api/v1/products/*/offers").permitAll()
					//.requestMatchers(HttpMethod.GET,"/api/v1/products/*/transactions").permitAll()

					.requestMatchers(HttpMethod.POST,"/api/v1/auth/*").permitAll()

					// PRIVATE ENDPOINTS
					.requestMatchers(HttpMethod.GET,"/api/v1/users").hasAnyRole("USER")
					.requestMatchers(HttpMethod.GET,"/api/v1/users/*/products").hasAnyRole("USER")
					.requestMatchers(HttpMethod.GET,"/api/v1/users/*/boughtProducts").hasAnyRole("USER")
					
					.requestMatchers(HttpMethod.POST,"/api/v1/users/*/products").hasAnyRole("USER")
					.requestMatchers(HttpMethod.POST,"/api/v1/users/*/products/*/ratings").hasAnyRole("USER")
					.requestMatchers(HttpMethod.POST,"/api/v1/products/*/offers").hasAnyRole("USER")
					.requestMatchers(HttpMethod.POST,"/api/v1/products/*/image").hasAnyRole("USER")

					.requestMatchers(HttpMethod.PUT,"/api/v1/users/*/products/*").hasAnyRole("USER", "ADMIN")
					.requestMatchers(HttpMethod.PUT,"/api/v1/users").hasAnyRole("USER", "ADMIN")
					.requestMatchers(HttpMethod.PUT,"/api/v1/products/*/image").hasAnyRole("USER", "ADMIN")
					.requestMatchers(HttpMethod.PUT,"/api/v1/users/*/image").hasAnyRole("USER", "ADMIN")
					
					.requestMatchers(HttpMethod.DELETE,"/api/v1/products/*").hasAnyRole("USER", "ADMIN")
			);
		
        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@Order(2)
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
				.requestMatchers("/api/**").permitAll()
				.requestMatchers("/v3/api-docs*/**").permitAll()
				.requestMatchers("/swagger-ui.html").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll()
				// PRIVATE PAGES
				.requestMatchers("/user/editProduct/*").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/user/submit_edit/*").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/product/*/delete").hasAnyRole("USER","ADMIN")
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
		return http.build();
	}

}