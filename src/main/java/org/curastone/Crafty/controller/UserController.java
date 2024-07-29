package org.curastone.Crafty.controller;

import org.curastone.Crafty.dao.UserDao;
import org.curastone.Crafty.model.UserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @Autowired UserDao userDao;

  @PostMapping("/register")
  public void register(@RequestBody UserRegistration user) {
    System.out.println("Received request to register user: " + user);
    userDao.save(user);
    System.out.println("User saved: " + user);
  }
}
