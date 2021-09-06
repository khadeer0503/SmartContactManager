package com.SmartContactManager.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SmartContactManager.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	@Query("select u from User u where u.email=:email")
  public User findByUserName(@Param("email") String email);
}
