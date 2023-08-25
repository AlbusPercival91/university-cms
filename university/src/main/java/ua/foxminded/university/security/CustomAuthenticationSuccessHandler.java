package ua.foxminded.university.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
			response.sendRedirect("/admin/main");
		} else if (authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("TEACHER"))) {
			response.sendRedirect("/teacher/main");
		} else if (authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("STUDENT"))) {
			response.sendRedirect("/student/main");
		} else if (authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("STAFF"))) {
			response.sendRedirect("/staff/main");
		} else {
			response.sendRedirect("/");
		}
	}

}
