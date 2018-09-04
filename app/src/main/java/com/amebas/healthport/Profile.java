package com.amebas.healthport;

import java.time.LocalDateTime;

public class Profile {

    public static final String DOB = "dob";
    public static final String NAME = "name";

    private LocalDateTime dob;
    private String name;
    private String documents;

    public Profile(){}

    public Profile(LocalDateTime dob, String name, String documents){
        this.dob = dob;
        this.name = name;
        this.documents = documents;
    }

    public LocalDateTime getDob() {
        return dob;
    }

    public void setDob(LocalDateTime dob) {
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
    }

    public void addDocuments(String document) {
        this.documents += "," + document;
    }
}
