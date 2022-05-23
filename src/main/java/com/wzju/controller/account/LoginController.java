package com.wzju.controller.account;

import com.wzju.service.AccountService;
import com.wzju.service.ExcelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class LoginResponse {
    public String code, token;

    public LoginResponse(String code, String token) {
        this.code = code;
        this.token = token;
    }
}

class RegisterResponse {
    public String code;

    public RegisterResponse(String code) {
        this.code = code;
    }
}

@RestController
public class LoginController {

    @Autowired
    AccountService accountService;

    @Autowired
    ExcelService excelService;

    @RequestMapping("/login")
    @CrossOrigin
    public LoginResponse login(String username, String password) {
        String token = accountService.login(username, password);
        if (token == null) {
            return new LoginResponse("404", null);
        } else {
            return new LoginResponse("200", token);
        }
    }

    @RequestMapping("/register")
    @CrossOrigin
    public RegisterResponse register(String username, String password) {
        if (accountService.register(username, password)) {
            excelService.createUserDir(username);
            return new RegisterResponse("200");
        } else {
            return new RegisterResponse("404");
        }
    }
}
