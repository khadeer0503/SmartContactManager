package com.SmartContactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PutMapping;
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
			//to set the image default if image is NULL in view contact from
			contact.setImage("contact.png");
		}else {
			contact.setImage(file.getOriginalFilename()); 
			
			//save the file in path then upload
			File saveFile = new ClassPathResource("static/images").getFile();
			
			//in path we have reached till images using (saveFile) + getting the file with name 
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			//Copies all bytes from an input stream to a file. On return, the inputstream will be at end of stream
			//By default, the copy fails if the target file already exists or is asymbolic link.
			//If the REPLACE_EXISTING option is specified, and the target file already exists,then it is replaced if it is not a non-empty directory
			
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
		//currentPage - page
		//contact per page - 3
		//setting the limit to the page is 3
		Pageable pageable = PageRequest.of(page, 3);
		
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts); 
		
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		
		m.addAttribute("title","View Contacts");
		return "Standard/viewContacts";
	}
	
	
	//to show the particular contact
	@RequestMapping("/{cid}/contact")
	public String particularContact(@PathVariable("cid") Integer cid,Model m,Principal principal)
	{
		
		Optional<Contact> findById = this.contactRepository.findById(cid);
		Contact Particularcontact = findById.get();

		String userName = principal.getName();
		User user = this.userRepository.findByUserName(userName);

		
		// security bug removing
		if(user.getId() == Particularcontact.getUser().getId()) {
			m.addAttribute("contact",Particularcontact);
			m.addAttribute("title",Particularcontact.getName());
		}
	
		 return "Standard/particularContact";
	}
	
	//deleting
	
	@GetMapping("/delete/{cid}")
	public String delete(@PathVariable("cid") Integer cid, Model m,HttpSession session,Principal principal)
	{
		Optional<Contact> findById = this.contactRepository.findById(cid);
		Contact contact = findById.get();
		
//		contact.setUser(null);
//		this.contactRepository.delete(contact); 
		
		User user = this.userRepository.findByUserName(principal.getName());
	user.getContact().remove(contact);
		
		this.userRepository.save(user);
		
		session.setAttribute("message",new Message("You successfully deleted the contact","success") );
		
		return "redirect:/user/viewContacts/0";
	}
	
	//update
	
	@PostMapping("/updateForm/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid ,Model m)
	{
		m.addAttribute("title","Update User");
		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		return "Standard/updateForm";
	}
	
	
	// update Process handler
	 @PostMapping("/updateFormProcess")
	public String updateFormProcess(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,Model m,
			HttpSession session,Principal principal)
	{
		try {
			//old contact details  
			Contact oldcontact = this.contactRepository.findById(contact.getCid()).get();
			
			//image
		if( ! file.isEmpty() )	{
			//rewrite the file
			
			
			//delete old images
			File deleteFile = new ClassPathResource("static/images").getFile();
			File file1= new File(deleteFile, oldcontact.getImage());
			file1.delete();
			
			//update new images
			File saveFile = new ClassPathResource("static/images").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			contact.setImage(file.getOriginalFilename());
			
		}else {
			contact.setImage(oldcontact.getImage());
		}
		
		User user = this.userRepository.findByUserName(principal.getName());
		contact.setUser(user);
		this.contactRepository.save(contact);
			
		session.setAttribute("message",new Message("Contact is Updated","success"));
		
		}catch (Exception e){
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	 
	 //your Profile
	 @GetMapping("/profile")
	 public String profile(Model m)
	 {
		 m.addAttribute("title","User Profile");
		 return "Standard/profile";
	 }
	 
	 
	 
	 
	 
	 
	 
}
