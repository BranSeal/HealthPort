package com.amebas.ref_u_store.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.amebas.ref_u_store.Model.SingleAction;
import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.R;

import java.util.Calendar;

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

    /**
     * Converts a date to a string.
     *
     * @param date  the date as a calendar instance.
     */
    public static String dateToString(Calendar date)
    {
        String day = Integer.toString(date.get(Calendar.DAY_OF_MONTH));
        if (day.length() == 1)
        {
            day = "0" + day;
        }
        String month = Integer.toString(date.get(Calendar.MONTH) + 1);
        if (month.length() == 1)
        {
            month = "0" + month;
        }
        String year = Integer.toString(date.get(Calendar.YEAR));
        return month + "/" + day + "/" + year;
    }

    /**
     * Converts a date string to a calendar instance.
     *
     * @param date  the date as a string.
     * @throws IllegalArgumentException if the string is not formatted properly (DD/MM/YYYY).
     */
    public static Calendar stringToDate(String date) throws IllegalArgumentException
    {
        String[] values = date.split("/");
        if (values.length != 3)
        {
            throw new IllegalArgumentException("Invalid date format - values not split by '/' character");
        }
        try
        {
            int month = Integer.parseInt(values[0]);
            int day = Integer.parseInt(values[1]) - 1;
            int year = Integer.parseInt(values[2]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            return calendar;
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid format in date - one or more values is not a number");
        }
    }

    /**
     * Checks whether a string is a valid password.
     *
     * @param pass  the password string to check.
     * @return if its a valid password.
     */
    public static boolean isValidPassword(String pass)
    {
        if (pass.length() >= 8)
        {
            return true;
        }
        return false;
    }
}
