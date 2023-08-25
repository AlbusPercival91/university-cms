package ua.foxminded.university.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String[] staticResources = { "/css/**", "/js/**" };

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().mvcMatchers(staticResources).permitAll().antMatchers("/", "/about", "/contacts")
				.permitAll().anyRequest().authenticated().and().formLogin()
				.successHandler(customAuthenticationSuccessHandler()).permitAll().and().logout().logoutUrl("/logout")
				.logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("JSESSIONID");
	}

	@Bean
	public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
		return new CustomAuthenticationSuccessHandler();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
