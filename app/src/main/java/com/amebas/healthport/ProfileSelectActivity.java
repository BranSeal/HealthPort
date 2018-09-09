package com.amebas.healthport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.time.LocalDate;
import java.util.ArrayList;

public class ProfileSelectActivity extends AppCompatActivity {

    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_select);

        String[] profiles = getProfilesForAccount();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_design, R.id.label, profiles);

        ListView listview = findViewById(R.id.profileList);
        listview.setAdapter(adapter);
    }

    //TODO Retrieve list of profiles for the currently logged in user and returns it
    private String[] getProfilesForAccount() {
        //profilesLen hardcoded rn
        int len = 4;
        String[] profileArr = new String[len];
        for (int i = 0; i < len; i++) {
            profileArr[i] = "TempName";
        }
        //test profiles
        //i cant call the function below due to api requirements?
        //LocalDate now = LocalDate.max();
        //Profile prof1 = new Profile(now, "Anush", null);
        return profileArr;
    }

    //TODO populates list view with all profiles
    public void populateListview(ArrayList<Profile> profiles) {

    }


}
