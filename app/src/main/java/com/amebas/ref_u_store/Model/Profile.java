package com.amebas.ref_u_store.Model;

import java.util.ArrayList;

/**
 * Profile Model
 */
public class Profile {

    private String id;
    private String dob;
    private String name;
    private ArrayList<Document> documents;

    public Profile(){
        this.documents = new ArrayList<>();
    }

    public Profile(String dob, String name, ArrayList<Document> documents){
        this.dob = dob;
        this.name = name;
        this.documents = documents;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }

    public void addDocuments(Document document) { this.documents.add(document); }

    public String toString() {
        return name + " " + dob;
    }

    public boolean equals(Profile p) {
        return this.id.equals(p.getId());
    }
}
