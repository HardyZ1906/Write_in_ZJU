package com.wzju.controller.login;

import com.wzju.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    
    @Autowired
    UserRepository userRepo;

    @RequestMapping("/login")
    @CrossOrigin
    public String login(String username, String password) {
        if (userRepo.login(username, password)) {
            return "200";
        } else {
            return "404";
        }
    }

    @RequestMapping("/register")
    @CrossOrigin
    public String register(String username, String password) {
        if (userRepo.register(username, password)) {
            return "200";
        } else {
            return "404";
        }
    }
}
