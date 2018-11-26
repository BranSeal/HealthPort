package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;
import com.amebas.ref_u_store.Utilities.Permissions;

import java.util.List;

public class ProfileSelectActivity extends AppCompatActivity {

    List<Profile> profiles;

    private int selected_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_select);

        String[] profiles = getProfilesForAccount();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_design, R.id.title, profiles);

        ListView listview = findViewById(R.id.profileList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(( parent, view, position, id) -> checkPermissions(position));
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
     */
    public void startDashboard(int position)
    {
        SessionManager instance = SessionManager.getInstance();
        Profile currentProfile = profiles.get(position);
        instance.setCurrentProfile(currentProfile);
        Intent dashboardIntent = new Intent(this ,AccountDashboardActivity.class);
        startActivity(dashboardIntent);
    }

    /**
     * Checks if read/write permissions are granted. If so, moves to dashboard of selected profile.
     * If not, waits for permissions result.
     *
     * @param position  The position of profile in list that was selected.
     */
    public void checkPermissions(int position)
    {
        if (Permissions.checkStoragePermissions(this))
        {
            startDashboard(position);
        }
        else
        {
            selected_position = position;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        boolean permission_granted = false;
        boolean counter = true;
        for (int result: grantResults)
        {
            if (result == PackageManager.PERMISSION_DENIED)
            {
                counter = false;
                break;
            }
        }
        if (counter)
        {
            permission_granted = true;
        }
        if (grantResults.length > 0 && permission_granted)
        {
            startDashboard(selected_position);
        }
        else
        {
            GeneralUtilities.showToast(this, getString(R.string.perm_failed));
            selected_position = 0;
        }
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
        GeneralUtilities.askConfirmation(this, getString(R.string.logout_confirm), new BinaryAction()
        {
            @Override
            public void confirmAction()
            {
                SessionManager.getInstance().setCurrentProfile(null);
                SessionManager.getInstance().setSessionAccount(null);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void denyAction() {}
        });
    }

    @Override
    public void onBackPressed()
    {
        // Log out
        logout(findViewById(R.id.logout_link));
    }
}
