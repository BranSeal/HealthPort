package com.amebas.ref_u_store.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.amebas.ref_u_store.Model.SingleAction;
import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.R;

public class GeneralUtilities {
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a confirmation popup with options to perform or deny.
     *
     * @param context  The context from which the confirmation is being called.
     * @param msg      The message to display.
     * @param actions  The actions to take upon receiving input from the user.
     */
    public static void askConfirmation(Context context, String msg, BinaryAction actions)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton(context.getString(R.string.yes), (dialog, id) ->
        {
            dialog.cancel();
            actions.confirmAction();
        });
        builder.setNegativeButton(context.getString(R.string.no), (dialog, id) ->
        {
            dialog.cancel();
            actions.denyAction();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displays a message on a popup window with a single confirmation option.
     *
     * @param context  The context from which the message is being displayed.
     * @param msg      The message to display.
     * @param action   The action to perform upon confirmation.
     */
    public static void displayMessage(Context context, String msg, SingleAction action)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton(context.getString(R.string.ok), (dialog, id) ->
        {
            dialog.cancel();
            action.perform();
        });
        builder.create().show();
    }
}
