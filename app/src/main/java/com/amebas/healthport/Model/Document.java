package com.amebas.healthport.Model;

import java.util.Arrays;

public class Document {

    private String[] referenceIDs;
    private String name;
    private String[] tags;

    public Document(){}

    public Document(String name) {
        this.name = name;
    }

    public Document(String name, String[] tags) {
        this.name = name;
        this.tags = tags;
    }

    public Document(String[] referenceIDs, String name, String[] tags, byte[] pictureBytes){
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

    public String getTagString() {
        return Arrays.toString(this.tags);
    }
}
