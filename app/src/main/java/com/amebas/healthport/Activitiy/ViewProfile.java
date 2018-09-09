package com.amebas.healthport.Activitiy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
    }

    public void returnToDashboard() {
        Intent dashboardIntent = new Intent(this, AccountDashboardActivity.class);
        startActivity(dashboardIntent);
    }
}
