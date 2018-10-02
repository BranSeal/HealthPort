package com.amebas.healthport.Utilities;

import android.content.Context;
import android.widget.Toast;

public class GeneralUtilities {
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
