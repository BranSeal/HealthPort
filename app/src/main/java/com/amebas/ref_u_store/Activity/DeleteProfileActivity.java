package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.amebas.ref_u_store.R;

/**
 * Activity for confirming the deletion of a profile.
 */
public class DeleteProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);
    }

    /**
     * Deletes profile and returns to profile select screen if valid password given.
     * If not, shows error snackbar.
     *
     * @param v  the view the action is called from.
     */
    public void delete(View v)
    {
        if (checkPassword(((TextInputEditText) findViewById(R.id.PasswordInput)).getText().toString()))
        {
            // TODO: delete profile
            return;
        }
        showSnackbar(getString(R.string.incorrect_password));
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
     * Checks inputted password.
     *
     * @param pass  the password inputted.
     * @return whether or not password is correct.
     */
    private boolean checkPassword(String pass)
    {
        // TODO: check password.
        return false;
    }

    /**
     * Displays a snackbar with a given message.
     *
     * @param msg   the message to display.
     */
    private void showSnackbar(String msg)
    {
        CoordinatorLayout coord = findViewById(R.id.coordinator);
        Snackbar snack = Snackbar.make(coord, msg, Snackbar.LENGTH_INDEFINITE);
        snack.setAction(R.string.ok, v -> snack.dismiss());
        snack.show();
    }
}
