package ua.foxminded.university.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

	@GetMapping("/")
	public String welcome() {
		return "home";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@GetMapping("/contacts")
	public String contacts() {
		return "contacts";
	}

}
