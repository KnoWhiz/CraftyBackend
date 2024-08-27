package org.curastone.Crafty.controller;

import org.curastone.Crafty.dao.UserDao;
import org.curastone.Crafty.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

  @Autowired private UserDao userDao;

  @PostMapping("/sign-up")
  public ResponseEntity<?> signUp(@RequestBody User user) {
    // Check if email already exists
    if (userDao.existsByEmail(user.getEmail())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use.");
    }
    // Hash user's password
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword);

    userDao.save(user);

    return ResponseEntity.ok("User registered successfully with ID: " + user.getId());
  }

  @PostMapping("/login")
  public String login(@RequestBody User info) {
    User user = userDao.findByEmail(info.getEmail());
    if (user != null && user.getPassword().equals(info.getPassword())) {
      return "Login successful";
    } else {
      return "Wrong email or password";
    }
  }

  @GetMapping("/settings")
  public User getUserSettings(@RequestHeader("Authorization") String token) {
    // TODO
    // Fetch by ID as a placeholder
    return userDao.findById("userId").orElse(null);
  }

  @PostMapping("/settings")
  public String updateUserSettings(
      @RequestHeader("Authorization") String token, @RequestBody User userDetails) {
    User user = userDao.findById("userId").orElse(null);
    if (user != null) {
      user.setOpenAiToken(userDetails.getOpenAiToken());
      userDao.save(user);
      return "Update successful";
    } else {
      return "User not found";
    }
  }

  //  @GetMapping
  //  public List<User> getAllUsers() {
  //    return userDao.findAll();
  //  }
  //
  //  @PostMapping("/register")
  //  public void register(@RequestBody User user) {
  //    System.out.println("Received request to register user: " + user);
  //    userDao.save(user);
  //    System.out.println("User saved: " + user);
  //  }
}
