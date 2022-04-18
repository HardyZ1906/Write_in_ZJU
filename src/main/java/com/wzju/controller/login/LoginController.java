package com.wzju.controller.login;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.wzju.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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

class TokenPoolCleanerThread extends Thread {

    private HashMap<String, Pair<String, Date>> tokenMap;
    
    @Override
    public void run() {
        for (;;) {
            try {
                sleep(1_000_000);  // cleans up token pool every 1000 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Iterator<Map.Entry<String, Pair<String, Date>>> it = tokenMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Pair<String, Date>> item = it.next();
                if (System.currentTimeMillis() - item.getValue().getSecond().getTime() > 1_000_000) {
                    it.remove();
                }
            }
        }
    }

    public TokenPoolCleanerThread(HashMap<String, Pair<String, Date>> tokenMap) {
        this.tokenMap = tokenMap;
    }
}

@RestController
public class LoginController {
    
    private HashMap<String, Pair<String, Date>> tokenMap;
    private TokenPoolCleanerThread cleanerThread;

    @Autowired
    UserRepository userRepo;

    @RequestMapping("/login")
    @CrossOrigin
    public LoginResponse login(String username, String password) {
        if (userRepo.login(username, password)) {
            String token = UUID.randomUUID().toString();
            tokenMap.put(token, Pair.of(username, new Date()));

            return new LoginResponse("200", token);
        } else {
            return new LoginResponse("404", null);
        }
    }

    @RequestMapping("/register")
    @CrossOrigin
    public RegisterResponse register(String username, String password) {
        if (userRepo.register(username, password)) {
            return new RegisterResponse("200");
        } else {
            return new RegisterResponse("404");
        }
    }

    public LoginController() {
        tokenMap = new HashMap<String, Pair<String, Date>>();
        cleanerThread = new TokenPoolCleanerThread(tokenMap);
        cleanerThread.start();
    }
}
