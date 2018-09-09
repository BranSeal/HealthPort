package com.amebas.healthport.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.amebas.healthport.Model.Account;
import com.amebas.healthport.Model.Profile;
import com.amebas.healthport.Model.SessionManager;
import com.amebas.healthport.R;

public class AccountDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_dashboard);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        SessionManager instance = SessionManager.getInstance();
        Profile currentProfile = instance.getCurrentProfile();
        getSupportActionBar().setTitle("Welcome " + currentProfile.getName() + "!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_dashboard_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                Intent profileIntent = new Intent(this, ViewProfile.class);
                startActivity(profileIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
