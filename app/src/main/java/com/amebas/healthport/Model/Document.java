package com.amebas.healthport.Model;

import java.util.ArrayList;

public class Document {

    // REFERENCEID SHOULD BE THE ABSOLUTE PATH FOR EACH IMAGE
    private ArrayList<String> referenceIDs;
    private String name;
    private ArrayList<String> tags;
    private String firebase_id;

    public Document(){
        this.referenceIDs = new ArrayList<>();
    }

    public Document(String name) {
        this.name = name;
    }

    public Document(String name, ArrayList<String> tags) {
        this.name = name;
        this.tags = tags;
    }

    public Document(ArrayList<String> referenceIDs, String name, ArrayList<String> tags){
        this.referenceIDs = referenceIDs;
        this.name = name;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirebase_id() {return firebase_id;}

    public void setFirebase_id(String firebase_id) {this.firebase_id = firebase_id;}

    public ArrayList<String> getReferenceIDs() { return referenceIDs; }

    public void setReferenceIDs(ArrayList<String> referenceIDs) {
        this.referenceIDs = referenceIDs;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getTagString() {
        return this.tags.toString();
    }
}
