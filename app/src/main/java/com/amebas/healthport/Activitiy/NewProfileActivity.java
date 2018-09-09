package com.amebas.healthport.Activitiy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amebas.healthport.Model.Profile;
import com.amebas.healthport.R;

public class NewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        Button done = (Button) findViewById(R.id.done);
        EditText firstName = (EditText) findViewById(R.id.firstName);
        EditText lastName = (EditText) findViewById(R.id.lastName);
        EditText dateOfBirth = (EditText) findViewById(R.id.dateOfBirth);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Todo: Add proper parsing for First Name, Last Name, and DOB before adding new profile
                Profile p = new Profile();

                //If valid input
               goToSelect();
            }
        });
    }

    public void goToProfileSelect(View view) {
        Intent profileSelect = new Intent(this, ProfileSelectActivity.class);
        startActivity(profileSelect);
    }
    public void goToSelect(){
        Intent profileSelect = new Intent(this, ProfileSelectActivity.class);
        startActivity(profileSelect);
    }
}