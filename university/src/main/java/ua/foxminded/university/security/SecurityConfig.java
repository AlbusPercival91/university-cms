package ua.foxminded.university.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String[] staticResources = { "/css/**", "/js/**" };

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().mvcMatchers(staticResources).permitAll()
				.antMatchers("/", "/about", "/contacts", "/admin/**").permitAll().anyRequest().authenticated().and()
				.formLogin().permitAll().and().logout().permitAll().and().httpBasic();
	}
}
