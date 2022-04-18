package com.wzju.controller.createDoc;

import java.util.Date;

import com.wzju.repository.DocRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateDocController {
    
    @Autowired
    DocRepository DocRepo;

    @RequestMapping("/createDoc")
    @CrossOrigin
    public Boolean createDoc(String filename, int type, String owner, Date dateCreated, String[] collaborators) {
        return true;
    }
}
