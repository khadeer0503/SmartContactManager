package com.SmartContactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.SmartContactManager.Dao.UserRepository;
import com.SmartContactManager.model.Contact;
import com.SmartContactManager.model.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@ModelAttribute
	public void addCommomData(Model m, Principal principal)
	{
		String userName = principal.getName();
		User user=this.userRepository.findByUserName(userName);
		m.addAttribute("user",user);
	}
	
	@RequestMapping("/dashboard")
	public String dashboard(Model m, Principal principal)
	{
		m.addAttribute("title","user Dashboard");
		return "Standard/UserDashboard";
	}
	
	@GetMapping("/addcontact")
	public String openContactForm(Model m)
	{
		m.addAttribute("title","Add Contact");
		m.addAttribute("contact",new Contact());
		return "Standard/addContactForm";
	}
	
	
	@PostMapping("/processForm")
	public String processForm(@ModelAttribute Contact contact,
					@RequestParam("profileImage")MultipartFile file, Principal principal)
	{
		try {
		//get the username
		String userName = principal.getName();
		User user=this.userRepository.findByUserName(userName);
		
		//processing image
		if(file.isEmpty())
		{
			//throw new Exception("File is Empty");
			System.out.println("File is Empty");
		}else {
			contact.setImage(file.getOriginalFilename());
			//save the file in path then upload
			File saveFile = new ClassPathResource("static/images").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}
		user.getContact().add(contact);
		contact.setUser(user);
		
		 this.userRepository.save(user);		
		}catch (Exception e) {
		e.printStackTrace();
		}
		return "Standard/addContactForm";
	}
	

}
