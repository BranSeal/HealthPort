package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;

import java.util.List;

public class ProfileSelectActivity extends AppCompatActivity {

    List<Profile> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_select);

        String[] profiles = getProfilesForAccount();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_design, R.id.title, profiles);

        ListView listview = findViewById(R.id.profileList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(( parent, view, position, id) -> {
            String profile = (String) parent.getItemAtPosition(position);
            startDashboard(profile, position);
        });
    }

    /**
     * This function retrieves the current profiles for the account logged in and displays them
     * @return
     */
    private String[] getProfilesForAccount() {
        SessionManager instance = SessionManager.getInstance();
        Account acc = instance.getSessionAccount();
        profiles = acc.getProfiles();
        if(profiles == null) {
            GeneralUtilities.showToast(getApplicationContext(), "Something went wrong, please login again");
            logout(findViewById(R.id.logout_link));
            return null;
        } else {
            String[] profileArr = new String[profiles.size()];
            for (int i = 0; i < profiles.size(); i++) {
                Profile prof = profiles.get(i);
                profileArr[i] = prof.getName();
            }
            return profileArr;
        }
    }

    /**
     * This function gets the necessary information from the profiles populated and starts the dashboard
     * @param profile
     */
    public void startDashboard(String profile, int position) {
        SessionManager instance = SessionManager.getInstance();
        Profile currentProfile = profiles.get(position);
        instance.setCurrentProfile(currentProfile);
        Intent dashboardIntent = new Intent(this ,AccountDashboardActivity.class);
        startActivity(dashboardIntent);
    }

    public void goToAddProfile(View view) {
        Intent createProfileIntent = new Intent(this, NewProfileActivity.class);
        startActivity(createProfileIntent);
    }

    /**
     * Logs user out and sends them back to landing screen.
     *
     * @param v  the view that calls the method.
     */
    public void logout(View v) {
        Intent landingIntent = new Intent(this, MainActivity.class);
        startActivity(landingIntent);
    }
}
