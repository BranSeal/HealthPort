package com.amebas.healthport.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amebas.healthport.Model.Account;
import com.amebas.healthport.Model.Profile;
import com.amebas.healthport.Model.SessionManager;
import com.amebas.healthport.R;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

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

        SpeedDialView speedDialView = findViewById(R.id.speedDial);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.firstFab, R.drawable.ic_file_upload_white_24dp)
                        .setLabel("Upload a File")
                        .setTheme(R.style.HealthPort)
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.secondFab, R.drawable.ic_camera_alt_white_24dp)
                        .setLabel("Take a Picture")
                        .setTheme(R.style.HealthPort)
                        .create()
        );

        int grey = getResources().getColor(R.color.superLightGreyButtonColor);
        FloatingActionButton mainFab = speedDialView.getMainFab();
        mainFab.setBackgroundTintList(ColorStateList.valueOf(grey));
        mainFab.setRippleColor(grey);

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.firstFab:
                        showToast("Pictures are worth five words");
                        return false; // true to keep the Speed Dial open
                    case R.id.secondFab:
                        showToast("So you wanna take a picture eh?");
                        return false; // true to keep the Speed Dial open
                    default:
                        return false;
                }
            }
        });

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

    public void showToast(String text) {
        Toast toast = Toast.makeText(
                getApplicationContext(),
                text,
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
