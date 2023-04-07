package com.spring.boot.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.spring.boot.vo.Menu;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@GetMapping
	public ModelAndView listUsers(Model model) {
		List<Menu> list = new ArrayList<>();
		list.add(new Menu("Manage users", "/users"));
		model.addAttribute("list", list);
		return new ModelAndView("/admin/index", "model", model);
	}

}
