package com.amebas.healthport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ProfileSelectActivity extends AppCompatActivity {

    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_select);

        ArrayList<Profile> profiles = getProfilesForAccount();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_design, mobileArray);

        ListView listview = findViewById(R.id.profileList);
        listview.setAdapter(adapter);
    }

    //TODO Retrieve list of profiles for the currently logged in user and returns it
    private ArrayList<Profile> getProfilesForAccount() {
        return new ArrayList<Profile>();
    }

    //TODO populates list view with all profiles
    public void populateListview(ArrayList<Profile> profiles) {

    }
}
