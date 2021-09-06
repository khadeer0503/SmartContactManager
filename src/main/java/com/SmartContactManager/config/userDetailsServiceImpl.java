package com.SmartContactManager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.SmartContactManager.Dao.UserRepository;
import com.SmartContactManager.model.User;

public class userDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	

	public userDetailsServiceImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}


	public userDetailsServiceImpl() {
		// TODO Auto-generated constructor stub
	}



	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByUserName(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("could not find the User");
		}
		customUserDetails customUserDetails = new customUserDetails(user);
		
		return customUserDetails;
	}

}
