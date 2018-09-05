package com.amebas.healthport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity
{
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
        // @TODO: error checking, and then send to database.
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

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        returnToStart();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
