package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.R;

import java.util.HashMap;
import java.util.Map;

public class NewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
    }

    public void goToProfileSelect(View view) {
        Intent profileSelect = new Intent(this, ProfileSelectActivity.class);
        startActivity(profileSelect);
    }
    public void goToSelect(){
        Intent profileSelect = new Intent(this, ProfileSelectActivity.class);
        startActivity(profileSelect);
    }

    /**
     * Attempts to create a new profile.
     */
    public void createProfile(View view)
    {
        Map<String, String> values = getFields();
        if (fieldsAreValid(values.get("first"), values.get("last"), values.get("dob")))
        {
            Profile p = new Profile(values.get("dob"), values.get("first") + " " + values.get("last"), null );
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.addProfile(p);
            //If valid input
            goToSelect();
        }
        else
        {
            Snackbar snack = Snackbar.make(view, getString(R.string.fields_empty), Snackbar.LENGTH_INDEFINITE);
            snack.setAction(R.string.ok, v -> snack.dismiss());
            snack.show();
        }
    }

    /**
     * Gets a map of the values inside of the text fields on the page.
     *
     * @return a map of the values of the text fields.
     */
    private Map<String, String> getFields()
    {
        HashMap<String, String> values = new HashMap();
        values.put("first", ((EditText) findViewById(R.id.firstName)).getText().toString());
        values.put("last", ((EditText) findViewById(R.id.lastName)).getText().toString());
        values.put("dob", ((EditText) findViewById(R.id.dateOfBirth)).getText().toString());
        return values;
    }

    /**
     * Validates inputs in the fields.
     *
     * @param first  first name.
     * @param last   last name.
     * @param dob    date of birth.
     * @return whether or not inputs were valid.
     */
    private boolean fieldsAreValid(String first, String last, String dob)
    {
        if (first.length() > 0 && last.length() > 0 && dob.length() > 0)
        {
            return true;
        }
        return false;
    }
}