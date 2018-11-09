package com.amebas.ref_u_store.Utilities;

import com.amebas.ref_u_store.Model.Document;

import java.util.ArrayList;

public class TagFilter {

    public static ArrayList<Document> filter(ArrayList<Document> list, String filter) {
        ArrayList<Document> filteredList = new ArrayList<>();
        for (Document d: list) {
            if (d.getName().contains(filter))
                filteredList.add(d);
        }
        if (filteredList.size() == 0)
            return null;
        else
            return filteredList;
    }

}
