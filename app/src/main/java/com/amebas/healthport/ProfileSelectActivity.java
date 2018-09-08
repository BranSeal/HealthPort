package com.amebas.healthport;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProfileSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_select);

        String[] profiles = getProfilesForAccount();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_design, R.id.label, profiles);

        ListView listview = findViewById(R.id.profileList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String profile = (String) parent.getItemAtPosition(position);
                //Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                startDashboard(profile);
            }
        });
    }

    //TODO Retrieve list of profiles for the currently logged in user and returns it
    private String[] getProfilesForAccount() {
        SessionManager instance = SessionManager.getInstance();
        Account acc = instance.getSessionAccount();
        List<Profile> profiles = acc.getProfiles();
        String[] profileArr = new String[profiles.size()];
        for(int i = 0; i < profiles.size(); i++) {
            Profile prof = profiles.get(i);
            profileArr[i] = prof.getName();
        }

        //test profiles
        //i cant call the function below due to api requirements?
        //LocalDate now = LocalDate.max();
        //Profile prof1 = new Profile(now, "Anush", null);
        return profileArr;
    }

    /**
     * This function gets the necessary information from the profiles populated and starts the dashboard
     * @param profile
     */
    public void startDashboard(String profile) {
        //TODO pass profile information over
        //TODO set active profile to profile selected
        Intent dashboardIntent = new Intent(this ,AccountDashboardActivity.class);
        startActivity(dashboardIntent);
    }
}
