package com.wzju.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("doc")
public class Doc {
    
    @Id
    public String id;
    
    @Field("filename")
    public String filename;

    @Field("type")
    public int type;

    @Field("owner")
    public String owner;

    @Field("dateCreated")
    public Date dateCreated;

    @Field("collaborators")
    public Set<String> collaborators;

    @Field("dateLastEdited")
    public Date dateLastEdited;

    @Field("lastEditor")
    public String lastEditor;

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getOwner() {
        return owner;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getFilename() {
        return filename;
    }

    public Set<String> getCollaborators() {
        return collaborators;
    }

    public void addCollaborator(String collaborator) {
        collaborators.add(collaborator);
    }

    public void addCollaborators(String[] collaborators) {
        for (String collaborator: collaborators) {
            this.collaborators.add(collaborator);
        }
    }

    public void dropCollaborator(String collaborator) {
        collaborators.remove(collaborator);
    }

    public void dropCollaborators(String[] collaborators) {
        for (String collaborator: collaborators) {
            this.collaborators.remove(collaborator);
        }
    }

    public Date getDateLastEdited() {
        return dateLastEdited;
    }

    public void setDateLastEdited(Date date) {
        dateLastEdited = date;
    }

    public String getLastEditor() {
        return lastEditor;
    }

    public void setLastEditor(String editor) {
        lastEditor = editor;
    }

    @PersistenceConstructor
    public Doc(String filename, int type, String owner, Set<String> collaborators,
               Date dateCreated, Date dateLastEdited, String lastEditor) {
        super();
        this.filename = filename;
        this.type = type;
        this.owner = owner;
        this.collaborators = collaborators;
        this.dateCreated = dateCreated;
        this.dateLastEdited = dateLastEdited;
        this.lastEditor = lastEditor;
    }

    public Doc(String filename, int type, String owner, String[] collaborators) {
        super();
        this.filename = filename;
        this.type = type;
        this.owner = owner;
        this.collaborators = new HashSet<>();
        for (String collaborator: collaborators) {
            this.collaborators.add(collaborator);
        }

        dateCreated = new Date();
        dateLastEdited = null;
        lastEditor = null;
    }
}
