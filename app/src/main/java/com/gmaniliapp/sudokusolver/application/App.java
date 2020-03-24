package com.gmaniliapp.sudokusolver.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.gmaniliapp.sudokusolver.R;
import com.google.android.material.snackbar.Snackbar;

import static com.gmaniliapp.sudokusolver.utils.Constants.APP_LINK;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    /**
     * Create a snackbar
     * @param view: View where display
     * @param message: Message to display
     * @return Snackbar
     */
    public Snackbar createSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.
                make(view, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        return snackbar;
    }

    /**
     * Display snackbar
     * @param view: View where display
     * @param message: Message to display
     */
    public void displaySnackbar(View view, String message) {
        Snackbar snackbar = createSnackbar(view, message);
        snackbar.show();
    }

    /**
     * Share app
     * @param context: Context from where sharing
     */
    public void share(Context context) {
        String message = getString(R.string.share_message, getString(R.string.app_name), APP_LINK);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }
}
