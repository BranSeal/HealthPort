package com.amebas.ref_u_store.Model;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Document {

    private static final String NAME_KEY = "NAME";
    private static final String ID_KEY = "ID";
    private static final String TAG_KEY = "TAGS";

    // REFERENCEID SHOULD BE THE ABSOLUTE PATH FOR EACH IMAGE
    private ArrayList<String> referenceIDs;
    private String name;
    private ArrayList<String> tags;
    private String firebase_id;

    public Document()
    {
        this(new ArrayList<>(), "", new ArrayList<>());
    }

    public Document(String name)
    {
        this(new ArrayList<>(), name, new ArrayList<>());
    }

    public Document(String name, ArrayList<String> tags)
    {
        this(new ArrayList<>(), name, tags);
    }

    public Document(ArrayList<String> referenceIDs, String name, ArrayList<String> tags)
    {
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

    /**
     * Gets a string representation of all the tags contained by the document.
     *
     * @return a string with all the document's tags, delimited by spaces.
     */
    public String getTagString() {
        String tags = "";
        for (String tag: this.tags)
        {
            tags += tag;
            tags += " ";
        }
        return tags.substring(0, tags.length() - 1);
    }

    /**
     * Converts a tag string back to a list of tags.
     *
     * @param tag_string  the tag string to convert.
     */
    public void setFromTagString(String tag_string)
    {
        ArrayList<String> tags = new ArrayList<>();
        for (String tag: tag_string.split(" "))
        {
            tags.add(tag);
        }
        this.setTags(tags);
    }

    /**
     * Converts a document to a Map to serialize.
     *
     * @return map of document's values.
     */
    public Map<String, String> toMap()
    {
        Map<String, String> values = new HashMap<>();
        values.put(Document.ID_KEY, this.referenceIDs.get(0)); // Should only be one anyways.
        values.put(Document.NAME_KEY, this.name);
        values.put(Document.TAG_KEY, this.getTagString());
        return values;
    }

    /**
     * Converts a map of a document's values to an instance.
     *
     * @param values  the values to convert into a document.
     * @return a document instance with the given values.
     */
    public static Document fromMap(Map<String, String> values)
    {
        Document doc = new Document();
        doc.setName((values.containsKey(Document.NAME_KEY) ? values.get(Document.NAME_KEY) : ""));
        ArrayList<String> ids = new ArrayList<>();
        if (values.containsKey(Document.ID_KEY))
        {
            ids.add(values.get(Document.ID_KEY));
        }
        doc.setReferenceIDs(ids);
        if (values.containsKey(Document.TAG_KEY))
        {
            doc.setFromTagString(values.get(Document.TAG_KEY));
        }
        else
        {
            ArrayList<String> tags = new ArrayList<>();
            doc.setTags(tags);
        }
        return doc;
    }
}
