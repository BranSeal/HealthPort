package com.amebas.ref_u_store.Activity;

import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.R;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EditProfile extends AppCompatActivity {
    Profile currProfile;
    TextView firstName;
    TextView lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstName = findViewById(R.id.firstNameEdit);
        lastName = findViewById(R.id.lastNameEdit);

        SessionManager instance = SessionManager.getInstance();
        Account account = instance.getSessionAccount();
        currProfile = instance.getCurrentProfile();

        String name = currProfile.getName();
        String[] names = name.split(" ");

        firstName.setText(names[0]);
        lastName.setText(names[1]);
    }

    public void saveProfile(View view) {
        String name = firstName.getText().toString() + ' ' + lastName.getText().toString();
        currProfile.setName(name);
    }

    public void goToViewProfile(View view) {
        Intent profileIntent = new Intent(this, ViewProfile.class);
        startActivity(profileIntent);
    }
}
