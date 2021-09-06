package com.SmartContactManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class Myconfig extends WebSecurityConfigurerAdapter {
	
@Bean
public UserDetailsService getService()
{
 return new userDetailsServiceImpl();	
}
//
@Bean
public BCryptPasswordEncoder passwordEncoder()
{
	return  new BCryptPasswordEncoder();
}
//
//private BCryptPasswordEncoder BCryptPasswordEncoder() {
//	// TODO Auto-generated method stub
//	return null;
//}

@Bean
public DaoAuthenticationProvider authenticationProvider()
{
	DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider();
	authenticationProvider.setUserDetailsService(this.getService());
	authenticationProvider.setPasswordEncoder(passwordEncoder());
	return authenticationProvider;
}

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.authenticationProvider(authenticationProvider());
}

@Override
protected void configure(HttpSecurity http) throws Exception {
	http.authorizeRequests()
	.antMatchers("/admin/**").hasRole("ADMIN")
	.antMatchers("/User/**").hasRole("STANDARD")
	.antMatchers("/**").permitAll().and().formLogin()
	.loginPage("/signin")
	.loginProcessingUrl("/dologin")
	.defaultSuccessUrl("/user/dashboard")
	.and().csrf().disable();
	
}



}
