package com.wzju.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("user")
public class User {
    
    @Id
    public String id;
    
    @Field("username")
    public String username;

    @Field("password")
    public String password;

    @Field("createdDocs")
    public Set<String> createdDocs;

    @Field("joinedDocs")
    public Set<String> joinedDocs;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getCreatedDocs() {
        return createdDocs;
    }

    public void addCreatedDoc(String doc) {
        createdDocs.add(doc);
    }

    public void dropCreatedDoc(String doc) {
        createdDocs.remove(doc);
    }

    public Set<String> getJoinedDocs() {
        return joinedDocs;
    }

    public void addJoinedDoc(String doc) {
        joinedDocs.add(doc);
    }

    public void dropJoinedDoc(String doc) {
        joinedDocs.remove(doc);
    }

    @PersistenceConstructor
    public User(String username, String password, Set<String> createdDocs, Set<String> joinedDocs) {
        super();
        this.username = username;
        this.password = password;
        this.createdDocs = createdDocs;
        this.joinedDocs = joinedDocs;
    }

    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
        this.createdDocs = new HashSet<>();
        this.joinedDocs  = new HashSet<>();
    }
}
