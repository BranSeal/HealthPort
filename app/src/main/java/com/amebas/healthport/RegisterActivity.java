package com.amebas.healthport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity
{
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
            Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);
            snack.setAction(R.string.ok_button, v -> snack.dismiss());
            snack.show();
            return;
        }
        Account acct = new Account(values.get("email"), values.get("password"), "");
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
        builder.setMessage("Are you sure you want to cancel?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", (dialog, id) -> {
            dialog.cancel();
            returnToStart();
        });
        builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }
}
