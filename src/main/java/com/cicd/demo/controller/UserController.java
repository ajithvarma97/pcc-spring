package com.cicd.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cicd.demo.model.User;
import com.cicd.demo.repository.UserRepository;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/get-email")
    public String getEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user.getEmail();
        }
        return "Email not found";
    }
}
