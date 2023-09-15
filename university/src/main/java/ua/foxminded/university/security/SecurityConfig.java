package ua.foxminded.university.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {
    private static final String[] staticResources = { "/css/**", "/js/**", "/img/**" };
    private static final String[] allowedPages = { "/home", "/about", "/contacts", "/course/course-list" };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests().mvcMatchers(staticResources).permitAll().mvcMatchers(allowedPages)
                .permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler()).permitAll().and().logout().logoutUrl("/logout")
                .logoutSuccessUrl("/home").invalidateHttpSession(true).deleteCookies("JSESSIONID").and().build();
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
