package controllers;

import com.mu.Controller;

import helpers.SoyTemplate;

public class Test extends Controller {
	public String home() {
		return SoyTemplate.render("test.helloWorld");
	}
}