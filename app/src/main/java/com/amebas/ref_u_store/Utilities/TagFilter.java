package com.amebas.ref_u_store.Utilities;

import java.util.ArrayList;

public class TagFilter {

    public static ArrayList<String> parse(ArrayList<String> list, String filter) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (String s : list) {
            if (s.contains(s))
                filteredList.add(s);
        }
        if (filteredList.size() == 0)
            return null;
        else
            return filteredList;
    }

}
