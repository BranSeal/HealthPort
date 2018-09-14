package com.amebas.healthport.Model;

import com.google.firebase.firestore.DocumentReference;

import java.time.LocalDate;
import java.util.List;

public class Profile {

    private String dob;
    private String name;
    private List<Document> documents;

    public Profile(){}

    public Profile(String dob, String name, List<Document> documents){
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

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void addDocuments(Document document) {
        this.documents.add(document);
    }
  
    public String toString() {
        return name + " " + dob;
    }

    public boolean equals(Profile p) {
        return this.name.equals(p.getName());
    }
}
