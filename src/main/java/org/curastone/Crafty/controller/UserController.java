package org.curastone.Crafty.controller;

import java.util.List;
import org.curastone.Crafty.dao.UserDao;
import org.curastone.Crafty.model.UserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @Autowired UserDao userDao;

  @GetMapping
  public List<UserRegistration> getAllUsers() {
    return userDao.findAll();
  }

  @PostMapping("/register")
  public void register(@RequestBody UserRegistration user) {
    System.out.println("Received request to register user: " + user);
    userDao.save(user);
    System.out.println("User saved: " + user);
  }
}
