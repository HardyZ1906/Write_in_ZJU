package com.wzju.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("Doc")
public class Doc {
    
    @Id
    private String filename;

    @Field("type")
    private int type;

    @Field("owner")
    private String owner;

    @Field("dateCreated")
    private Date dateCreated;

    @Field("collaborators")
    private String[] collaborators;

    @Field("dateLastEdited")
    private Date dateLastEdited;

    @Field("lastEditor")
    private String lastEditor;

    public Doc(String filename, int type, String owner, Date dateCreated,
               String[] collaborators, Date dateLastEdited, String lastEditor) {
        this.filename = filename;
        this.type = type;
        this.owner = owner;
        this.dateCreated = dateCreated;
        this.collaborators = collaborators;
        this.dateLastEdited = dateLastEdited;
        this.lastEditor = lastEditor;
    }
}
