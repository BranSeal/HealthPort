package com.amebas.ref_u_store.Model;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Class that encapsulates a document's data, without enclosing the actual file.
 */
public class Document
{
    private static final String NAME_KEY = "NAME";
    private static final String PATH_KEY = "PATH";
    private static final String TAG_KEY = "TAGS";
    private static final String ID_KEY = "ID";

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
        doc.setId((values.containsKey(Document.ID_KEY) ? values.get(Document.ID_KEY) : ""));
        doc.setPath((values.containsKey(Document.PATH_KEY) ? values.get(Document.PATH_KEY) : ""));
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

    private String firebase_id;
    private String name;
    private String path;
    private ArrayList<String> tags;

    /**
     * Initializes a document with all parameters.
     *
     * @param id    the ID of a document in Firebase.
     * @param name  the name of a document.
     * @param path  the path to a document in local storage.
     * @param tags  a list of tags for the document.
     */
    public Document(String id, String name, String path, ArrayList<String> tags)
    {
        this.firebase_id = id;
        this.name = name;
        this.path = path;
        this.tags = tags;
    }

    /**
     * Initializes a document without a Firebase ID (pre-upload).
     *
     * @param name  the name of the document.
     * @param path  the path to a document in local storage.
     * @param tags  a list of tags for the document.
     */
    public Document(String name, String path, ArrayList<String> tags)
    {
        this ("", name, path, tags);
    }

    /**
     * Initializes a document without a Firebase ID or tags.
     *
     * @param name  the name of the document.
     * @param path  the path to a document in local storage.
     */
    public Document(String name, String path)
    {
        this("", name, path, new ArrayList<>());
    }

    /**
     * Initializes a document with only a name.
     *
     * @param name  the name of a document.
     */
    public Document(String name)
    {
        this("", name, "", new ArrayList<>());
    }

    /**
     * Initializes an empty document.
     */
    public Document()
    {
        this("", "", "", new ArrayList<>());
    }

    /**
     * Gets a document's ID in Firebase.
     *
     * @return the document's ID.
     */
    public String getId()
    {
        return firebase_id;
    }

    /**
     * Sets a document's ID.
     *
     * @param id  the ID to set for the document.
     */
    public void setId(String id)
    {
        this.firebase_id = id;
    }

    /**
     * Gets a document's name.
     *
     * @return the document's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets a document's name.
     *
     * @param name  the name to set for the document.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets the document's path in local storage.
     *
     * @return the document's path.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the document's path in local storage.
     *
     * @param path  the path to set for the document.
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Gets the list of tags for a document.
     *
     * @return the list of tags.
     */
    public ArrayList<String> getTags()
    {
        return tags;
    }

    /**
     * Sets the list of tags for a document.
     *
     * @param tags  the list of tags to set for the document.
     */
    public void setTags(ArrayList<String> tags)
    {
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
        values.put(Document.PATH_KEY, this.path); // Should only be one anyways.
        values.put(Document.NAME_KEY, this.name);
        values.put(Document.TAG_KEY, this.getTagString());
        values.put(Document.ID_KEY, this.firebase_id);
        return values;
    }

    @Override
    /**
     * Checks based on firebase ID.
     */
    public boolean equals(Object that)
    {
        if (that instanceof Document)
        {
            Document that_doc = (Document) that;
            if (this.firebase_id.equals(that_doc.firebase_id))
            {
                return true;
            }
        }
        return false;
    }
}
