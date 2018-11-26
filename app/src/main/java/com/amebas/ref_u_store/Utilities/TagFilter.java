package com.amebas.ref_u_store.Utilities;

import com.amebas.ref_u_store.Model.Document;

import java.util.ArrayList;

public class TagFilter {

    public static ArrayList<Document> filter(ArrayList<Document> list, String filter) {
        ArrayList<Document> filteredList = new ArrayList<>();
        String f = filter.toLowerCase();
        for (Document d: list) {
            String name = d.getName().toLowerCase();
            if (name.contains(f))
                filteredList.add(d);
        }
        return filteredList;
    }
}
