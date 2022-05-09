package com.wzju.controller.account;

import java.util.List;

import com.wzju.model.User;
import com.wzju.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class GetAllUsersResponse {
    public String code;
    public List<User> users;

    public GetAllUsersResponse(String code, List<User> users) {
        this.code = code;
        this.users = users;
    }
}

@RestController
public class TmpController {
    
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    AccountService accountService;

    @RequestMapping("/getAllUsers")
    @CrossOrigin
    public GetAllUsersResponse getAllUsers(String token) {
        if (accountService.validate(token) == null) {
            return new GetAllUsersResponse("400", null);
        } else {
            return new GetAllUsersResponse("200", accountService.getAllUsers());
        }
    }
}
