package com.amebas.healthport.Model;

import java.util.ArrayList;

public class Document {

    // REFERENCEID SHOULD BE THE ABSOLUTE PATH FOR EACH IMAGE
    private ArrayList<String> referenceIDs;
    private String name;
    private ArrayList<String> tags;

    public Document(){}

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
        return (this.tags != null) ? this.tags.toString() : "[]";
    }
}
