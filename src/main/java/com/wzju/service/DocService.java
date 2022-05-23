package com.wzju.service;

import java.io.IOException;

import com.wzju.model.Doc;
import com.wzju.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


@Service
public class DocService {
    
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ExcelService excelService;

    public int createDoc(String username, String filename, int type, String[] collaborators) {
        System.out.println("createDoc: " + username + ", " + filename + ", " + type);

        Query query;
        User user;
        Doc doc;

        try {
            switch (type) {
            case 0:
                // TODO: create a .txt file
                break;
            case 1:
                excelService.createExcelFile("./excelfiles/" + username + "/" + filename + ".xls");
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        doc = new Doc(filename, type, username, collaborators);
        try {
            mongoTemplate.insert(doc, "doc");
        } catch (DuplicateKeyException e) {
            System.out.println("...  failed");
            return -2;
        }

        query = new Query(Criteria.where("username").is(username));
        if ((user = mongoTemplate.findOne(query, User.class)) != null) {
            user.addCreatedDoc(doc.getId());
            mongoTemplate.save(user);
        }

        for (String collaborator: collaborators) {
            query = new Query(Criteria.where("username").is(collaborator));
            if ((user = mongoTemplate.findOne(query, User.class)) != null) {
                user.addJoinedDoc(doc.getId());
                mongoTemplate.save(user);
            }
        }

        System.out.println("...  success");

        return 0;
    }

    public int changeCollaborators(String owner, String filename, int type, String[] toAdd, String[] toDrop) {
        System.out.println("changeCollaborators: " + owner + ", " + filename + ", " + type);
        
        Query query;
        Doc doc;
        User user;

        query = new Query();
        query.addCriteria(Criteria.where("owner").is(owner));
        query.addCriteria(Criteria.where("filename").is(filename));
        query.addCriteria(Criteria.where("type").is(type));
        // System.out.println(query.toString());

        if ((doc = mongoTemplate.findOne(query, Doc.class)) == null) {
            System.out.println("...  not found");
            return -1;
        } else {
            for (String username: toAdd) {
                query = new Query(Criteria.where("username").is(username));
                if ((user = mongoTemplate.findOne(query, User.class)) != null) {
                    user.addJoinedDoc(doc.getId());
                    mongoTemplate.save(user);
                }
            }

            for (String username: toDrop) {
                query = new Query(Criteria.where("username").is(username));
                if ((user = mongoTemplate.findOne(query, User.class)) != null) {
                    user.dropJoinedDoc(doc.getId());
                    mongoTemplate.save(user);
                }
            }

            doc.addCollaborators(toAdd);
            doc.dropCollaborators(toDrop);
            mongoTemplate.save(doc);

            System.out.println("...  success");
        }

        return 0;
    }

    public int dropDoc(String owner, String filename, int type) {
        System.out.println("dropDoc: " + owner + ", " + filename + ", " + type);

        Query query;
        Doc doc;

        query = new Query();
        query.addCriteria(Criteria.where("owner").is(owner));
        query.addCriteria(Criteria.where("filename").is(filename));
        query.addCriteria(Criteria.where("type").is(type));
        // System.out.println(query.toString());

        if ((doc = mongoTemplate.findOne(query, Doc.class)) == null) {
            System.out.println("...  failed");
            return -1;
        } else {
            mongoTemplate.remove(query);

            User user;

            query = new Query(Criteria.where("username").is(doc.getOwner()));
            if ((user = mongoTemplate.findOne(query, User.class)) != null) {
                user.dropCreatedDoc(doc.getId());
                mongoTemplate.save(user);
            }

            for (String collaborator: doc.getCollaborators()) {
                query = new Query(Criteria.where("username").is(collaborator));
                if ((user = mongoTemplate.findOne(query, User.class)) != null) {
                    user.dropJoinedDoc(doc.getId());
                    mongoTemplate.save(user);
                }
            }

            System.out.println("...  success");

            return 0;
        }
    }
}
