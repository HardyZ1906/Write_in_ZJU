package com.wzju.controller.doc;

import java.util.List;

import com.wzju.model.Doc;
import com.wzju.service.AccountService;
import com.wzju.service.DocService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class GetCreatedDocsResponse {
    public String code;
    public List<Doc> docs;

    public GetCreatedDocsResponse(String code, List<Doc> docs) {
        this.code = code;
        this.docs = docs;
    }
}

class GetJoinedDocsResponse {
    public String code;
    public List<Doc> docs;

    public GetJoinedDocsResponse(String code, List<Doc> docs) {
        this.code = code;
        this.docs = docs;
    }
}

@RestController
public class ViewDocController {
    
    @Autowired
    AccountService accountService;

    @Autowired
    DocService docService;

    @RequestMapping("/getCreatedDocs")
    @CrossOrigin
    public GetCreatedDocsResponse getCreatedDocs(String token) {
        String username = accountService.validate(token);
        if (username == null) {
            return new GetCreatedDocsResponse("400", null);
        } else {
            return new GetCreatedDocsResponse("200", accountService.getCreatedDocs(username));
        }
    }

    @RequestMapping("/getJoinedDocs")
    @CrossOrigin
    public GetJoinedDocsResponse getJoinedDocs(String token) {
        String username = accountService.validate(token);
        if (username == null) {
            return new GetJoinedDocsResponse("400", null);
        } else {
            return new GetJoinedDocsResponse("200", accountService.getJoinedDocs(username));
        }
    }
}
