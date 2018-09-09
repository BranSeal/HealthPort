package com.amebas.healthport;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity
{
    private static final int MIN_PASSWORD_LENGTH = 8;

    private FirebaseFirestore db;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        dbManager = new DatabaseManager(db);
    }

    /**
     * @TODO Action to complete upon pressing the Register button.
     *
     * @param view  the view carrying out the method.
     */
    public void register(View view)
    {
        Map<String, String> values = getFields();
        String msg = checkFields(values.get("email"), values.get("password"), values.get("pass_confirm"));
        if (msg.length() > 0)
        {
            showSnackbar(view, msg);
            return;
        }
        Account acct = new Account(values.get("email"), values.get("password"));
        addAccount(acct);
    }

    /**
     * Cancels registration and returns to the launch screen. If any of the fields are filled,
     * asks for confirmation to prevent accidentally starting over.
     *
     * @param view  the view carrying out the method.
     */
    public void cancel(View view)
    {
        Map<String, String> values = getFields();
        for (String key: values.keySet())
        {
            if (values.get(key).length() > 0)
            {
                showCancelAlert();
                return;
            }
        }
        returnToStart();
    }

    /**
     * Checks for any invalid inputs for account creation.
     *
     * @param email         the value in the email field.
     * @param pass          the value in the password field.
     * @param confirm_pass  the value in the password confirmation field.
     * @return error message, if any.
     */
    public String checkFields(String email, String pass, String confirm_pass)
    {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            return getString(R.string.email_not_valid);
        }
        if (pass.length() < MIN_PASSWORD_LENGTH)
        {
            return getString(R.string.pass_not_long);
        }
        if (!pass.equals(confirm_pass))
        {
            return getString(R.string.pass_not_matching);
        }
        return "";
    }

    /**
     * Gets a map of the values inside of the text fields on the page.
     *
     * @return a map of the values of the text fields.
     */
    private Map<String, String> getFields()
    {
        HashMap<String, String> values = new HashMap();
        values.put("email", ((EditText) findViewById(R.id.email_input)).getText().toString());
        values.put("password", ((EditText) findViewById(R.id.password_input)).getText().toString());
        values.put("pass_confirm", ((EditText) findViewById(R.id.confirm_password_input)).getText().toString());
        return values;
    }

    /**
     * Returns the app to the Launch activity.
     */
    private void returnToStart()
    {
        Intent landingPageIntent = new Intent(this, MainActivity.class);
        startActivity(landingPageIntent);
    }

    /**
     * Displays the confirmation alert for canceling registration.
     */
    private void showCancelAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.cancel_confirm));
        builder.setCancelable(true);

        builder.setPositiveButton(getString(R.string.yes), (dialog, id) -> {
            dialog.cancel();
            returnToStart();
        });
        builder.setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displays confirmation alert for successfully registering for application.
     */
    private void showConfirmationAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.signup_confirm));
        builder.setCancelable(true);

        builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
            dialog.cancel();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displays a snackbar with a given message.
     *
     * @param view  the view to search for root from.
     * @param msg   the message to display.
     */
    private void showSnackbar(View view, String msg)
    {
        Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);
        snack.setAction(R.string.ok, v -> snack.dismiss());
        snack.show();
    }

    /**
     * Create an account in the database. If account already exists, fails. If successful, display
     * confirmation alert. If failed for any other reason, display snackbar.
     *
     * @param acct  the account to create.
     */
    private void addAccount(Account acct)
    {
        Toast toast = Toast.makeText(this, R.string.signup_in_progress, Toast.LENGTH_LONG);
        toast.show();
        // Check if account exists.
//        if (!dbManager.doesAccountExist())
//        {
//            showSnackbar(findViewById(R.id.reg_coord_layout), getString(R.string.email_exists));
//            return;
//        }
//        dbManager.addAccount(acct)
//            .addOnSuccessListener(result ->
//            {
//                toast.cancel();
//                showConfirmationAlert();
//            })
//            .addOnFailureListener(result ->
//            {
//                showSnackbar(findViewById(R.id.reg_coord_layout), getString(R.string.reg_failed));
//            });
    }
}
