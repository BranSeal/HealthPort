package com.amebas.ref_u_store.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.amebas.ref_u_store.Model.AccountValidator;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.R;

/**
 * Activity for confirming the deletion of a profile.
 */
public class DeleteProfileActivity extends AppCompatActivity {

    private Snackbar snack; // Currently displayed snackbar.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        // Makes sure snackbar is cleared when text input is clicked, since
        // adjust pan doesn't work when snackbar is visible.
        TextInputEditText input = findViewById(R.id.PasswordInput);
        input.setOnClickListener(view -> clearSnack());
    }

    /**
     * Deletes profile and returns to profile select screen if valid password given.
     * If not, shows error snackbar.
     *
     * @param v  the view the action is called from.
     */
    public void delete(View v)
    {
        snack = showSnackbar("Checking password...");
        SessionManager.getInstance().getDatabase().checkCredentials(
            SessionManager.getInstance().getSessionAccount().getEmail(),
            ((TextInputEditText) findViewById(R.id.PasswordInput)).getText().toString(),
            new AccountValidator()
            {
                @Override
                public void invalidEmail() {
                    clearSnack();
                    Log.d("ERROR", "Email could not be found in the database.");
                }

                @Override
                public void invalidPass() {
                    snack = showSnackbar(getString(R.string.incorrect_password));
                }

                @Override
                public void validCredentials() {
                    Profile current = SessionManager.getInstance().getCurrentProfile();
                    SessionManager.getInstance().getDatabase().deleteProfile(
                        current,
                        SessionManager.getInstance().getSessionAccount(),
                        task -> {
                            if (task.isSuccessful())
                            {
                                clearSnack();
                                SessionManager.getInstance().deleteProfile(current);
                                showConfirmationAlert();
                            }
                            else
                            {
                                Log.d("ERROR", "Failed to delete profile");
                                showSnackbar(getString(R.string.failed_delete));
                            }
                        }
                    );
                }
            }
        );
    }

    /**
     * Returns the user to the profile details screen.
     *
     * @param v  the view that the action is called from.
     */
    public void cancel(View v)
    {
        Intent intent = new Intent(this, ViewProfile.class);
        startActivity(intent);
    }

    /**
     * Clears the currently visible snackbar, if one exists.
     */
    private void clearSnack()
    {
        if (snack != null)
        {
            snack.dismiss();
        }
    }

    /**
     * Displays a snackbar with a given message.
     *
     * @param msg   the message to display.
     * @return the snackbar being displayed.
     */
    private Snackbar showSnackbar(String msg)
    {
        clearSnack();
        Snackbar snack = Snackbar.make(findViewById(R.id.coordinator), msg, Snackbar.LENGTH_INDEFINITE);
        snack.setAction(R.string.ok, v -> snack.dismiss());
        snack.show();
        return snack;
    }

    /**
     * Displays confirmation alert for successfully registering for application.
     */
    private void showConfirmationAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirm));
        builder.setCancelable(true);

        builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
            dialog.cancel();
            Intent selectProfileIntent = new Intent(this, ProfileSelectActivity.class);
            startActivity(selectProfileIntent);
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
