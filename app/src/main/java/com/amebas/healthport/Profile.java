package com.amebas.healthport;

public class Profile {

    public static final String DOB = "dob";
    public static final String NAME = "name";

    private String dob;
    private String name;
    private String documents;

    public Profile(){}

    public Profile(String dob, String name, String documents){
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
