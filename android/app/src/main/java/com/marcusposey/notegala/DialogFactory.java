package com.marcusposey.notegala;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

/** Constructs instances of AlertDialog */
public class DialogFactory {
    /**
     * Constructs a dialog that confirms the deletion of a resource
     *
     * @param context The context where the dialog will be displayed
     * @param resourceName The name of the resource to display in the dialog title
     * @param onYes Called if the yes button is pressed
     */
    public static AlertDialog deletion(Context context, String resourceName, Runnable onYes) {
        return new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_delete) + " " + resourceName)
                .setMessage(context.getString(R.string.dialog_delete_message))
                .setPositiveButton(android.R.string.yes, (dialog, btn) -> onYes.run())
                .setNegativeButton(android.R.string.no, null)
                .create();
    }

    @FunctionalInterface
    public interface InputListener {
        void onConfirmed(String input);
    }

    /**
     * Constructs a dialog that takes text as input
     *
     * @param context The context where the dialog will be displayed
     * @param title The dialog title text
     * @param onOk Called with the input text if the ok button is pressed
     */
    public static AlertDialog input(Context context, String title, InputListener onOk) {
        EditText content = new EditText(context);
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(content)
                .setPositiveButton(android.R.string.yes,
                        (dialog, btn) -> onOk.onConfirmed(content.getText().toString()))
                .setNegativeButton(android.R.string.no, null)
                .create();
    }
}
