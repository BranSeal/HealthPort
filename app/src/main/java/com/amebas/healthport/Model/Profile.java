package com.amebas.healthport.Model;

import com.google.firebase.firestore.DocumentReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Profile {

    private String dob;
    private String name;
    private ArrayList<Document> documents;

    public Profile(){}

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

    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }

    public void addDocuments(Document document) {
        if(this.documents == null) {
            ArrayList<Document> arrList = new ArrayList<>();
            arrList.add(document);
            this.documents = arrList;
        } else {
            this.documents.add(document);
        }

    }
  
    public String toString() {
        return name + " " + dob;
    }

    public boolean equals(Profile p) {
        return this.name.equals(p.getName());
    }
}
