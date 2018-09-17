package com.amebas.healthport.Model;

public class Document {

    private String[] referenceIDs;
    private String name;
    private String[] tags;

    public Document(){}

    public Document(String[] referenceIDs, String name, String[] tags){
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

    public String[] getReferenceIDs() { return referenceIDs; }

    public void setReferenceIDs(String[] referenceIDs) {
        this.referenceIDs = referenceIDs;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
