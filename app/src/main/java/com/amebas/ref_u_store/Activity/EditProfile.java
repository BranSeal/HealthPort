package com.amebas.ref_u_store.Activity;

import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.DatabaseManager;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.R;

import android.content.Intent;
import android.os.ProxyFileDescriptorCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class EditProfile extends AppCompatActivity {
    SessionManager instance;
    Profile oldProfile;
    Profile currProfile;
    TextView firstName;
    TextView lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstName = findViewById(R.id.firstNameEdit);
        lastName = findViewById(R.id.lastNameEdit);

        instance = SessionManager.getInstance();
        oldProfile = instance.getCurrentProfile();
        currProfile = oldProfile.clone();

        String name = currProfile.getName();
        String[] names = name.split(" ");

        firstName.setText(names[0]);
        lastName.setText(names[1]);
    }

    public void saveProfile(View view) {
        Account account = instance.getSessionAccount();
        String name = firstName.getText().toString() + ' ' + lastName.getText().toString();
        currProfile.setName(name);
        instance.setCurrentProfile(currProfile);

        DatabaseManager db = instance.getDatabase();
        db.updateProfile(currProfile);
        // TODO: Add ability to update currently existing profile because right now it will just add a new profile with new name
        Log.d("Anush: Old Profile", oldProfile.toString());
        Log.d("Anush: New Profile", currProfile.toString());
        goToViewProfile(null);
    }

    public void goToViewProfile(View view) {
        Intent profileIntent = new Intent(this, ViewProfile.class);
        startActivity(profileIntent);
    }
}
