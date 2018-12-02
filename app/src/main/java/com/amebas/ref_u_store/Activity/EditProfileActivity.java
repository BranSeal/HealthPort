package com.amebas.ref_u_store.Activity;

import com.amebas.ref_u_store.Model.DatabaseManager;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //populates the edit texts with current name and (in the future) other changeable information
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SessionManager instance = SessionManager.getInstance();
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
        EditText date_field = findViewById(R.id.dateOfBirth);
        firstNameText.setText(firstName);
        lastNameText.setText(lastName);
        date_field.setText(profile.getDob());
    }

    /**
     * Saves profile changes to database and updates current logged in profile
     *
     * @param view  view that calls the method.
     */
    public void updateProfile(View view)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseManager dbManager = new DatabaseManager(db);
        SessionManager instance = SessionManager.getInstance();
        Profile profile = instance.getCurrentProfile();

        String firstName = ((EditText) findViewById(R.id.firstNameEdit)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.lastNameEdit)).getText().toString();
        String name = firstName + " " + lastName;
        String dob = ((EditText) findViewById(R.id.dateOfBirth)).getText().toString();

        Profile new_profile = new Profile();
        new_profile.setDob(dob);
        new_profile.setName(name);
        new_profile.setId(profile.getId());

        dbManager.updateProfile(new_profile, () ->
        {
            profile.setName(name);
            profile.setDob(dob);
            GeneralUtilities.displayMessage(this, getString(R.string.profile_update_success), () ->
            {
                Intent intent = new Intent(this, ViewProfile.class);
                this.startActivity(intent);
            });
        }, () -> GeneralUtilities.displayMessage(this, getString(R.string.profile_update_failure), () -> {}));
    }

    public void cancel(View view)
    {
        finish();
    }

    /**
     * Displays a date picker and adds the selected value to the date of birth field.
     *
     * @param v  the view that called the method.
     */
    public void showDatePicker(View v)
    {
        EditText date_field = findViewById(R.id.dateOfBirth);
        String date_string = date_field.getText().toString();
        Calendar c;
        if (date_string.length() == 0)
        {
            c = Calendar.getInstance();
        }
        else
        {
            c = GeneralUtilities.stringToDate(date_string);
        }
        int init_year = c.get(Calendar.YEAR);
        int init_month = c.get(Calendar.MONTH);
        int init_day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, (view, year, month, day) ->
        {
            Calendar date = Calendar.getInstance();
            date.set(year, month, day);
            date_field.setText(GeneralUtilities.dateToString(date));
        }, init_year, init_month, init_day).show();
    }
}
