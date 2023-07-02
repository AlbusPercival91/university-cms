package ua.foxminded.university.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/css/**", "/js/**").permitAll() // Allow access to CSS and JS resources
				.antMatchers("/").permitAll() // Allow access to the home page
				.anyRequest().authenticated() // Require authentication for other URLs
				.and().formLogin().loginPage("/login").permitAll(); // Customize login page URL
	}
}
