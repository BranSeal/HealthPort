package com.amebas.ref_u_store.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.Model.DatabaseManager;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;
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
        String msg = checkFields(values.get("email"), values.get("phone"), values.get("password"), values.get("pass_confirm"));
        if (msg.length() > 0)
        {
            showSnackbar(view, msg);
            return;
        }
        Account acct = new Account(values.get("email"), values.get("password"));
        acct.setPhoneNumber(values.get("phone"));
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
     * @param phone         the value in the phone number field.
     * @param pass          the value in the password field.
     * @param confirm_pass  the value in the password confirmation field.
     * @return error message, if any.
     */
    public String checkFields(String email, String phone, String pass, String confirm_pass)
    {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            return getString(R.string.email_not_valid);
        }
        if (!PhoneNumberUtils.isGlobalPhoneNumber(phone))
        {
            return getString(R.string.phone_not_valid);
        }
        if (!GeneralUtilities.isValidPassword(pass))
        {
            return getString(R.string.pass_not_long);
        }
        if (!pass.equals(confirm_pass))
        {
            return getString(R.string.pass_not_matching);
        }
        return "";
    }

    @Override
    public void onBackPressed()
    {
        // Does same actions as canceling registration.
        cancel(findViewById(R.id.cancel_link));
    }

    /**
     * Gets a map of the values inside of the text fields on the page.
     *
     * @return a map of the values of the text fields.
     */
    private Map<String, String> getFields()
    {
        String phone = ((EditText) findViewById(R.id.phone_input)).getText().toString();
        phone = phone.replaceAll("[\\s-()]+","");
        HashMap<String, String> values = new HashMap();
        values.put("email", ((EditText) findViewById(R.id.email_input)).getText().toString());
        values.put("phone", phone);
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
        GeneralUtilities.askConfirmation(this, getString(R.string.cancel_confirm), new BinaryAction()
        {
            @Override
            public void confirmAction()
            {
                returnToStart();
            }

            @Override
            public void denyAction() {}
        });
    }

    /**
     * Displays confirmation alert for successfully registering for application.
     */
    private void showConfirmationAlert()
    {
        GeneralUtilities.displayMessage(this, getString(R.string.signup_confirm), () ->
        {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }

    /**
     * Displays a snackbar with a given message.
     *
     * @param view  the view to search for root from.
     * @param msg   the message to display.
     */
    private void showSnackbar(View view, String msg)
    {
        CoordinatorLayout layout = findViewById(R.id.reg_coord_layout);
        Snackbar snack = Snackbar.make(layout, msg, Snackbar.LENGTH_INDEFINITE);
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
        //Check if account exists.
        dbManager.doesAccountExist(acct.getEmail())
            .addOnSuccessListener(result ->
            {
                if (result.exists())
                {
                    toast.cancel();
                    showSnackbar(findViewById(R.id.reg_coord_layout), getString(R.string.email_exists));
                }
                else
                {
                    // Only add account if fails (account doesn't already exist).
                    dbManager.addAccount(acct)
                        .addOnSuccessListener(result2 ->
                        {
                            toast.cancel();
                            showConfirmationAlert();
                        })
                        .addOnFailureListener(result2 ->
                            showSnackbar(findViewById(R.id.reg_coord_layout), getString(R.string.reg_failed))
                        );
                }
            })
            .addOnFailureListener(result ->
                showSnackbar(findViewById(R.id.reg_coord_layout), getString(R.string.reg_failed))
            );

    }
}
