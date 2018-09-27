package com.amebas.healthport.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amebas.healthport.Model.Account;
import com.amebas.healthport.Model.Profile;
import com.amebas.healthport.Model.SessionManager;
import com.amebas.healthport.R;


public class ViewProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        populateText();
    }

    /**
     * This function populates the textviews on the page with pertinent information
     */
    public void populateText() {
        SessionManager instance = SessionManager.getInstance();
        Account account = instance.getSessionAccount();
        Profile profile = instance.getCurrentProfile();
        TextView nameContent = findViewById(R.id.nameContent);
        TextView dobContent = findViewById(R.id.dobContent);
        TextView emailContent = findViewById(R.id.emailContent);

        nameContent.setText(profile.getName());
        dobContent.setText(profile.getDob());
        emailContent.setText(account.getEmail());
    }

    public void returnToDashboard(View view) {
        Intent dashboardIntent = new Intent(this, AccountDashboardActivity.class);
        startActivity(dashboardIntent);
    }
}
