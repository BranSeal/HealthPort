package com.amebas.ref_u_store.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.AccountValidator;
import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;

import java.util.HashMap;

/**
 * Activity for editing details of an account.
 */
public class EditAccountActivity extends AppCompatActivity
{
    private String oldPhone;
    private Snackbar current_snack;

    private boolean is_validating;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        Account account = SessionManager.getInstance().getSessionAccount();
        oldPhone = account.getPhoneNumber();
        oldPhone = oldPhone == null ? "" : oldPhone;
        ((EditText) findViewById(R.id.phoneInput)).setText(oldPhone);
    }

    /**
     * Submits the inputted values to update the account.
     *
     * @param v  the view that calls the method.
     */
    public void submit(View v)
    {
        HashMap<String, String> values = getValues();
        String phone_value = values.get("phone");
        phone_value = phone_value.replaceAll("[\\s-()]+","");
        final String phone = phone_value;
        final String pass = values.get("pass");
        String pass_confirm = values.get("pass_confirm");
        String error_msg = checkInputs(phone, pass, pass_confirm);
        if (error_msg.length() > 0)
        {
            showSnackbar(error_msg);
        }
        else
        {
            if (current_snack != null)
            {
                current_snack.dismiss();
            }
            Context context = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getString(R.string.enter_current_pass));
            View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_input_password, findViewById(R.id.background), false);
            final EditText input = viewInflated.findViewById(R.id.input);
            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) ->
            {
                if (!is_validating)
                {
                    is_validating = true;
                    String email = SessionManager.getInstance().getSessionAccount().getEmail();
                    String password = input.getText().toString();
                    builder.setTitle(getString(R.string.validating));
                    SessionManager.getInstance().getDatabase().checkCredentials(email, password, new AccountValidator()
                    {
                        @Override
                        public void invalidEmail() {}

                        @Override
                        public void invalidPass()
                        {
                            is_validating = false;
                            dialog.dismiss();
                            GeneralUtilities.displayMessage(context, getString(R.string.incorrect_password), () -> {});
                        }

                        @Override
                        public void validCredentials()
                        {
                            Account current_account = SessionManager.getInstance().getSessionAccount();
                            Account account = new Account(email, current_account.getPassword());
                            account.setPhoneNumber(oldPhone);
                            if (pass.length() > 0)
                            {
                                account.setPassword(pass);
                            }
                            if (!oldPhone.equals(phone))
                            {
                                account.setPhoneNumber(phone);
                            }
                            SessionManager.getInstance().getDatabase().updateAccount(account, () ->
                            {
                                if (pass.length() > 0)
                                {
                                    current_account.setPassword(pass);
                                }
                                if (!oldPhone.equals(phone))
                                {
                                    current_account.setPhoneNumber(phone);
                                }
                                is_validating = false;
                                dialog.dismiss();
                                GeneralUtilities.displayMessage(context, getString(R.string.account_update_success), () ->
                                {
                                    Intent intent = new Intent(context, ViewProfile.class);
                                    context.startActivity(intent);
                                });
                            }, () ->
                            {
                                is_validating = false;
                                dialog.dismiss();
                                GeneralUtilities.displayMessage(context, getString(R.string.account_update_failure), () -> {});
                            });
                        }
                    });
                }
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }

    /**
     * Exits the edit account screen.
     *
     * @param v  the view that called the method.
     */
    public void cancel(View v)
    {
        HashMap<String, String> values = getValues();
        if (!values.get("phone").equals(oldPhone) || values.get("pass").length() > 0 || values.get("pass_confirm").length() > 0)
        {
            GeneralUtilities.askConfirmation(this, getString(R.string.cancel_confirm), new BinaryAction()
            {
                @Override
                public void confirmAction()
                {
                    finish();
                }

                @Override
                public void denyAction() { }
            });
        }
        else
        {
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        // Does same actions as canceling registration.
        cancel(findViewById(R.id.cancelButton));
    }

    /**
     * Gets the data in all the input fields.
     *
     * @return map of all the values in the input fields.
     */
    private HashMap<String, String> getValues()
    {
        HashMap<String, String> values = new HashMap<>();
        values.put("phone", ((EditText) findViewById(R.id.phoneInput)).getText().toString());
        values.put("pass", ((EditText) findViewById(R.id.newPass)).getText().toString());
        values.put("pass_confirm", ((EditText) findViewById(R.id.newPassConfirm)).getText().toString());

        return values;
    }

    /**
     * Checks if the given inputs are valid.
     *
     * @param phone         the input value for the phone number.
     * @param pass          the input value for the new password.
     * @param pass_confirm  the input value for the new password confirmation.
     * @return empty string if all valid, error message if not.
     */
    private String checkInputs(String phone, String pass, String pass_confirm)
    {
        if (phone.equals(oldPhone) && pass.length() == 0 && pass_confirm.length() == 0)
        {
            return getString(R.string.no_changes);
        }
        if (!phone.equals(oldPhone) && !PhoneNumberUtils.isGlobalPhoneNumber(phone))
        {
            return getString(R.string.phone_not_valid);
        }
        if (pass.length() > 0 || pass_confirm.length() > 0)
        {
            if (!pass.equals(pass_confirm))
            {
                return getString(R.string.pass_not_matching);
            }
            if (!GeneralUtilities.isValidPassword(pass))
            {
                return getString(R.string.pass_not_long);
            }
        }
        return "";
    }

    /**
     * Shows a snackbar that pushes the screen up.
     *
     * @param msg  the message to display.
     */
    private void showSnackbar(String msg)
    {
        CoordinatorLayout layout = findViewById(R.id.reg_coord_layout);
        current_snack = Snackbar.make(layout, msg, Snackbar.LENGTH_INDEFINITE);
        current_snack.setAction(R.string.ok, v -> current_snack.dismiss());
        current_snack.show();
    }
}
