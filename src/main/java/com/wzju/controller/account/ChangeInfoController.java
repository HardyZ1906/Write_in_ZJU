package com.wzju.controller.account;

import com.wzju.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class ChangePswResponse {
    public String code;

    public ChangePswResponse(String code) {
        this.code = code;
    }
}

@RestController
public class ChangeInfoController {

    @Autowired
    AccountService accountService;

    @RequestMapping("/changePasswd")
    @CrossOrigin
    public ChangePswResponse changePasswd(String token, String newPsw) {
        String username = accountService.validate(token);
        if (username == null) {
            return new ChangePswResponse("400");
        } else if (accountService.changePasswd(username, newPsw)) {
            return new ChangePswResponse("200");
        } else {
            return new ChangePswResponse("404");
        }
    }
}
