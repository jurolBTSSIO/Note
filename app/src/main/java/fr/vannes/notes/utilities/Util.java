package fr.vannes.notes.utilities;

import android.content.Context;
import android.widget.Toast;

public class Util {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
