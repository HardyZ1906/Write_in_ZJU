package com.wzju.service;

import java.util.*;

import com.wzju.model.Doc;
import com.wzju.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


class TokenRec {
    String username;
    Date lastSession;

    public TokenRec(String username, Date lastSession) {
        this.username = username;
        this.lastSession = lastSession;
    }
}


class TokenPoolCleanerThread extends Thread {

    private HashMap<String, TokenRec> tokenMap;
    
    @Override
    public void run() {
        for (;;) {
            try {
                sleep(1_000_000);  // cleans up token pool every 1000 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Iterator<Map.Entry<String, TokenRec>> it = tokenMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, TokenRec> item = it.next();
                if (System.currentTimeMillis() - item.getValue().lastSession.getTime() > 1_000_000) {
                    it.remove();
                }
            }
        }
    }

    public TokenPoolCleanerThread(HashMap<String, TokenRec> tokenMap) {
        this.tokenMap = tokenMap;
    }

}


@Service
public class AccountService {
    
    private HashMap<String, TokenRec> tokenMap;
    private TokenPoolCleanerThread cleanerThread;

    @Autowired
    MongoTemplate mongoTemplate;

    public String login(String username, String password) {
        System.out.println("login: " + username + ", " + password);

        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        query.addCriteria(Criteria.where("password").is(password));
        
        if (mongoTemplate.findOne(query, User.class) == null) {
            System.out.println("... not found");
            return null;
        } else {
            String token = UUID.randomUUID().toString();
            tokenMap.put(token, new TokenRec(username, new Date()));
            System.out.println("... success");
            return token;
        }
    }

    public Boolean register(String username, String password) {
        System.out.println("register: " + username + ", " + password);

        User user = new User(username, password);
        try {
            mongoTemplate.insert(user, "user");
        } catch (DuplicateKeyException e) {
            System.out.println("... failed");
            return false;
        }
        System.out.println("... success");
        return true;
    }

    public Boolean changePasswd(String username, String newPsw) {
        System.out.println("changePasswd: " + username + ", " + newPsw);

        Query query = new Query();
        Update update = new Update();

        query.addCriteria(Criteria.where("username").is(username));
        update.set("password", newPsw);
        if (mongoTemplate.upsert(query, update, User.class).getMatchedCount() == 0) {
            System.out.println("... failed");
            return false;
        } else {
            System.out.println("... success");
            return true;
        }
    }

    public List<Doc> getCreatedDocs(String username) {
        System.out.println("getCreatedDocs: " + username);

        Query query = new Query();
        User user;

        query.addCriteria(Criteria.where("username").is(username));
        if ((user = mongoTemplate.findOne(query, User.class)) == null) {
            System.out.println("... not found");
            return null;
        } else {
            Set<String> docIds = user.getCreatedDocs();
            List<Doc> docs = new ArrayList<>();
            for (String docId: docIds) {
                Doc doc = mongoTemplate.findById(docId, Doc.class);
                if (doc != null) {
                    docs.add(doc);
                }
            }
            System.out.println("... success");
            return docs;
        }
    }

    public List<Doc> getJoinedDocs(String username) {
        System.out.println("getJoinedDocs: " + username);

        Query query = new Query();
        User user;

        query.addCriteria(Criteria.where("username").is(username));
        if ((user = mongoTemplate.findOne(query, User.class)) == null) {
            System.out.println("... not found");
            return null;
        } else {
            Set<String> docIds = user.getJoinedDocs();
            List<Doc> docs = new ArrayList<>();
            for (String docId: docIds) {
                Doc doc = mongoTemplate.findById(docId, Doc.class);
                if (doc != null) {
                    docs.add(doc);
                }
            }
            System.out.println("... success");
            return docs;
        }
    }

    public String validate(String token) {
        System.out.println("validate: " + token);

        TokenRec tokenRec = tokenMap.get(token);
        if (tokenRec == null) {
            System.out.println("... not found");
            return null;
        } else {
            System.out.println("... success");
            tokenRec.lastSession = new Date();
            return tokenRec.username;
        }
    }

    public List<User> getAllUsers() {
        System.out.println("getAllUsers...");

        Query query = new Query();
        query.fields().include("username").exclude("id");
        return mongoTemplate.find(query, User.class);
    }

    public AccountService() {
        tokenMap = new HashMap<>();
        cleanerThread = new TokenPoolCleanerThread(tokenMap);
        cleanerThread.start();
    }

}
