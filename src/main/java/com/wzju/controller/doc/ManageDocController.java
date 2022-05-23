package com.wzju.controller.doc;

import com.wzju.service.AccountService;
import com.wzju.service.DocService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class CreateDocResponse {
    public String code;

    public CreateDocResponse(String code) {
        this.code = code;
    }
}

class ChangeCollaboratorsResponse {
    public String code;

    public ChangeCollaboratorsResponse(String code) {
        this.code = code;
    }
}

class DropDocResponse {
    public String code;

    public DropDocResponse(String code) {
        this.code = code;
    }
}

@RestController
public class ManageDocController {

    @Autowired
    AccountService accountService;

    @Autowired
    DocService docService;

    @RequestMapping("/createDoc")
    @CrossOrigin
    public CreateDocResponse createDoc(String token, String filename, int type, String[] collaborators) {
        String username = accountService.validate(token);

        if (username == null) {
            return new CreateDocResponse("400");
        } else if (docService.createDoc(username, filename, type, collaborators) < 0) {
            return new CreateDocResponse("404");
        } else {
            return new CreateDocResponse("200");
        }
    }

    @RequestMapping("/changeCollaborators")
    @CrossOrigin
    public ChangeCollaboratorsResponse changeCollaborators(String token, String filename, int type,
        String[] toAdd, String[] toDrop) {
        String username = accountService.validate(token);

        if (username == null) {
            return new ChangeCollaboratorsResponse("400");
        } else if (docService.changeCollaborators(username, filename, type, toAdd, toDrop) < 0) {
            return new ChangeCollaboratorsResponse("404");
        } else {
            return new ChangeCollaboratorsResponse("200");
        }
    }

    @RequestMapping("/dropDoc")
    @CrossOrigin
    public DropDocResponse dropDoc(String token, String filename, int type) {
        String username = accountService.validate(token);

        if (username == null) {
            return new DropDocResponse("400");
        } else if (docService.dropDoc(username, filename, type) < 0) {
            return new DropDocResponse("404");
        } else {
            return new DropDocResponse("200");
        }
    }
}
