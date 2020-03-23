package com.gmaniliapp.sudokusolver.application;

import android.app.Application;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

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
}
