package com.wzju.repository;

import com.wzju.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class UserRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public Boolean login(String username, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        query.addCriteria(Criteria.where("password").is(password));
        
        if (mongoTemplate.findOne(query, User.class) == null) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean register(String username, String password) {
        User user = new User(username, password);
        try {
            mongoTemplate.insert(user, "user");
        } catch (DuplicateKeyException e) {
            return false;
        }

        return true;
    }
}
