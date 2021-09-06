package com.SmartContactManager.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.SmartContactManager.Dao.UserRepository;
import com.SmartContactManager.helper.Message;
import com.SmartContactManager.model.User;

@Controller
public class mainController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String Home(Model m)
	{
		//to pass the title dynamically from controller and from base.html
		m.addAttribute("title","Home - Smart Contact Management ");
		return "Home";
	}
	
	@RequestMapping("/about")
	public String About(Model m)
	{
		//to pass the title dynamically from controller and from base.html
		m.addAttribute("title","About - Smart Contact Management ");
		return "About";
	}
	
	@RequestMapping("/signup")
	public String SignUp(Model m)
	{
		//to pass the title dynamically from controller and from base.html
		m.addAttribute("title","SignUp - Smart Contact Management ");
		m.addAttribute("user",new User());
		return "SignUp";
	}
	
	//retrieve the form data
	@PostMapping("/do-register")
	public String Register(@Validated @ModelAttribute("user") User user ,BindingResult r,
			@RequestParam(value="agreement", defaultValue = "false")boolean agreement ,
			Model model,HttpSession session)// throws Exception
	{
		System.out.println("agreement"+agreement);
		System.out.println("User"+user);
		
	
	try {
		
		if(!agreement)
		{
			System.out.println("you have not agree the terms and condition");
			throw new Exception("you have not agree the terms and condition");
		}
		if(r.hasErrors()) {
			model.addAttribute("user",user);
			System.out.println("this is from Binding result"+r.toString());
			return "SignUp";
		}		
		user.setRole("Standard_User");
		user.setEnabled(true);
		user.setImage("default.png");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		System.out.println("Agreement"+agreement);
		System.out.println("user"+ user);
		
		User result = this.userRepository.save(user);//to save the role,enabled,image 
		//model.addAttribute("user",result);
		
		model.addAttribute("user", new User());
		session.setAttribute("message", new Message("successfully registered !!","alert-success"));
		return "SignUp";
		
	} catch (Exception e) {
		e.printStackTrace();
		
		//model.addAttribute("user", user);
		session.setAttribute("message", new Message("something went wrong !!"+e.getMessage(),"alert-danger"));
		return "SignUp";
	}
	}
	
	
//handler for custom login
	@GetMapping("signin")
	public String customLogin(Model m)
	{
		m.addAttribute("title","Login Page");
		return "Standard/signin";
	}

}
