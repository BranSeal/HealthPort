package com.amebas.ref_u_store.Utilities;

import android.content.Context;
import android.widget.Toast;

public class GeneralUtilities {
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
