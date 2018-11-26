package com.amebas.ref_u_store.Activity;

import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.DatabaseManager;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //populates the edit texts with current name and (in the future) other changeable information
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SessionManager instance = SessionManager.getInstance();
        Account account = instance.getSessionAccount();
        Profile profile = instance.getCurrentProfile();

        String fullName = profile.getName();
        String[] names = fullName.split(" ");
        String lastName = "";
        if(names.length > 1) {
            lastName = names[1];
        }
        String firstName = names[0];

        EditText firstNameText = findViewById(R.id.firstNameEdit);
        EditText lastNameText = findViewById(R.id.lastNameEdit);
        firstNameText.setText(firstName);
        lastNameText.setText(lastName);
    }

    /**
     * Saves profile changes to database and updates current logged in profile
     * @param view unecessary view passed in from the xml
     */
    public void updateProfile(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseManager dbManager = new DatabaseManager(db);
        SessionManager instance = SessionManager.getInstance();
        Account account = instance.getSessionAccount();
        Profile profile = instance.getCurrentProfile();

        EditText firstNameText = findViewById(R.id.firstNameEdit);
        EditText lastNameText = findViewById(R.id.lastNameEdit);
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();

        profile.setName(firstName + " " + lastName);
        dbManager.updateProfile(profile);
        instance.setCurrentProfile(profile);
        GeneralUtilities.showToast(getApplicationContext(), "Changes Saved");
        cancel(null);
    }

    public void cancel(View view) {
        Intent viewProfileIntent = new Intent(this, ViewProfile.class);
        startActivity(viewProfileIntent);

    }
}
