package com.amebas.ref_u_store.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Tools for requesting permissions. Make sure to override onRequestPermissionsResult in activity
 * that permissions are asked for.
 */
public class Permissions
{
    public static final int REQUEST_CODE = 15;

    /**
     * Checks whether read/write to external storage permissions are given.
     *
     * @param activity  the activity being called in.
     * @return whether or not permissions were already granted.
     */
    public static boolean checkStoragePermissions(Activity activity)
    {
        return checkPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    /**
     * Checks whether camera permissions are given.
     *
     * @param activity  the activity being called in.
     * @return whether or not permissions were already granted.
     */
    public static boolean checkCameraPermissions(Activity activity)
    {
        return checkPermissions(activity, new String[]{Manifest.permission.CAMERA});
    }

    /**
     * Checks whether given permissions are granted.
     *
     * @param activity     the activity being called in.
     * @param permissions  a list of permissions to check.
     * @return whether or not permissions were already granted.
     */
    private static boolean checkPermissions(Activity activity, String[] permissions)
    {
        ArrayList<String> to_ask = new ArrayList<>();
        for (String permission: permissions)
        {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
            {
                to_ask.add(permission);
            }
        }
        if (to_ask.size() > 0)
        {
            ActivityCompat.requestPermissions(activity, to_ask.toArray(new String[0]), REQUEST_CODE);
            return false;
        }
        else
        {
            return true;
        }
    }
}
