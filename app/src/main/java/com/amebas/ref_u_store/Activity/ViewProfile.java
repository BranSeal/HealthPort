package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;


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
        TextView phoneContent = findViewById(R.id.phoneContent);

        // Format phone number if US number.
        String phone = account.getPhoneNumber();
        if (phone != null)
        {
            if (phone.length() == 10)
            {
                phone = "(" + phone.substring(0, 3) + ") "
                        + phone.substring(3, 6) + "-"
                        + phone.substring(6, 10);
            }
        }
        else
        {
            phone = getString(R.string.default_empty);
        }

        nameContent.setText(profile.getName() == null ? getString(R.string.default_empty) : profile.getName());
        dobContent.setText(profile.getDob() == null ? getString(R.string.default_empty) : profile.getDob());
        emailContent.setText(account.getEmail() == null ? getString(R.string.default_empty) : account.getEmail());
        phoneContent.setText(phone);
    }

    /**
     * Goes to the account dashboard screen.
     *
     * @param view  the view that called the method.
     */
    public void returnToDashboard(View view) {
        Intent dashboardIntent = new Intent(this, AccountDashboardActivity.class);
        startActivity(dashboardIntent);
    }

    /**
     * Displays the menu.
     *
     * @param v  view that called the method.
     */
    public void showMenu(View v)
    {
        PopupMenu menu = new PopupMenu(this, v);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.profile_options, menu.getMenu());
        // Set last item to red.
        MenuItem item = menu.getMenu().getItem(menu.getMenu().size() - 1);
        SpannableString s = new SpannableString(getString(R.string.logout));
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);
        // Set listener
        menu.setOnMenuItemClickListener(menu_item ->
        {
            switch (menu_item.getItemId())
            {
                case R.id.profile_edit:
                    editProfile();
                    return true;
                case R.id.profile_switch:
                    switchProfile();
                    return true;
                case R.id.account_edit:
                    editAccount();
                    return true;
                case R.id.logout:
                    logout();
                    return true;
                default:
                    return false;
            }
        });
        menu.show();
    }

    @Override
    public void onBackPressed()
    {
        // Always return to dashboard on back button. Prevents return to edit menu.
        returnToDashboard(findViewById(R.id.overallView));
    }

    /**
     * Moves to the edit profile screen.
     */
    private void editProfile()
    {
        Intent editProfile = new Intent(getApplicationContext(), EditProfileActivity.class);
        startActivity(editProfile);
    }

    /**
     * "Signs out" of a profile, returning to profile select screen.
     */
    private void switchProfile()
    {
        SessionManager.getInstance().setCurrentProfile(null);
        Intent intent = new Intent(this, ProfileSelectActivity.class);
        startActivity(intent);
    }

    /**
     * Moves to the edit account screen.
     */
    private void editAccount()
    {
        Intent editAccount = new Intent(getApplicationContext(), EditAccountActivity.class);
        startActivity(editAccount);
    }

    /**
     * Logs the user out of the account.
     */
    private void logout()
    {
        GeneralUtilities.askConfirmation(this, getString(R.string.logout_confirm), new BinaryAction()
        {
            @Override
            public void confirmAction()
            {
                SessionManager.getInstance().setCurrentProfile(null);
                SessionManager.getInstance().setSessionAccount(null);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void denyAction() {}
        });
    }
}
