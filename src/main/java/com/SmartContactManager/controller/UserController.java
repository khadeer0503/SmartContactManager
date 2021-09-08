package com.SmartContactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.SmartContactManager.Dao.ContactRepository;
import com.SmartContactManager.Dao.UserRepository;
import com.SmartContactManager.helper.Message;
import com.SmartContactManager.model.Contact;
import com.SmartContactManager.model.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//common data to get the user details
	@ModelAttribute
	public void addCommomData(Model m, Principal principal)
	{
		String userName = principal.getName();
		User user=this.userRepository.findByUserName(userName);
		m.addAttribute("user",user);
	}
	
	@RequestMapping("/dashboard")
	public String dashboard(Model m)
	{
		m.addAttribute("title"," Dashboard");
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
					@RequestParam("profileImage")MultipartFile file,Model m, Principal principal,HttpSession session  )
	{
		try {
			m.addAttribute("title","Contact Added !!");
		//get the userName
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
		 session.setAttribute("message", new Message("Contact Added !!","success"));
		}catch (Exception e) {
		e.printStackTrace();
		session.setAttribute("message", new Message("Something went Wrong !!","danger"));
		}
		return "Standard/addContactForm";
	}
	
	@GetMapping("/viewContacts/{page}")
	public String viewContacts(@PathVariable("page") Integer page,Model m,Principal principal)
	{
		String userName = principal.getName();
		User user = this.userRepository.findByUserName(userName);
		
		
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts); 
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		m.addAttribute("title","View Contacts");
		return "Standard/viewContacts";
	}
	
	
}
