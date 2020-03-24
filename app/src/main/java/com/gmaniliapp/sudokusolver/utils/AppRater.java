package com.gmaniliapp.sudokusolver.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.gmaniliapp.sudokusolver.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.Date;

import static com.gmaniliapp.sudokusolver.utils.Constants.APP_PACKAGE;

public class AppRater {
    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 4;

    private Context context;
    private int count;

    public AppRater(Context context) {
        this.context = context;
        scheduleRate();
    }

    private void scheduleRate() {
        PreferenceManager preferenceManager = new PreferenceManager(context, "apprater");

        // Don't show again
        if (preferenceManager.getBoolean("dontshowagain", false))
            return;

        count = preferenceManager.getInt("count", 0) +1;
        preferenceManager.putInt("count", count);

        // Check launches since last remind
        int lastCount;
        if (preferenceManager.getBoolean("remindmelater", false)) {
            lastCount = preferenceManager.getInt("remind_count", 0);
            if (lastCount + LAUNCHES_UNTIL_PROMPT > count)
                return;
        }

        Date lastLaunch = preferenceManager.getDate("date_last_launch");
        if (lastLaunch == null) {
            lastLaunch = new Date();
            preferenceManager.putDate("date_last_launch", lastLaunch);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastLaunch);
        calendar.add(Calendar.DATE, DAYS_UNTIL_PROMPT);
        Date nextLaunch = calendar.getTime();

        // Check on launches and date interval
        if (count >= LAUNCHES_UNTIL_PROMPT) {
            if (new Date().compareTo(nextLaunch) >= 0) {
                displayDialog();
            }
        }
    }

    /**
     * Display dialog to rate app
     */
    private void displayDialog() {
        // Create dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.MaterialDialogTheme)
                .setTitle(context.getString(R.string.rate_app))
                .setMessage(context.getString(R.string.confirmation_rate_app, context.getString(R.string.app_name)))
                .setPositiveButton(context.getString(R.string.yes), null)
                .setNegativeButton(context.getString(R.string.dont_ask_again), null)
                .setNeutralButton(context.getString(R.string.remind_me_later), null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(view1 -> {
                // Avoid creation of dialog in the future and rate
                PreferenceManager preferenceManager = new PreferenceManager(context,"apprater");
                preferenceManager.putBoolean("dontshowagain", true);
                rate();
                dialog.dismiss();
            });
            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negative.setOnClickListener(view1 -> {
                // Avoid creation of dialog in the future
                PreferenceManager preferenceManager = new PreferenceManager(context,"apprater");
                preferenceManager.putBoolean("dontshowagain", true);
                dialog.dismiss();
            });
            Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutral.setOnClickListener(view1 -> {
                // Save status of this launch
                PreferenceManager preferenceManager = new PreferenceManager(context,"apprater");
                preferenceManager.putInt("remind_count", count);
                preferenceManager.putBoolean("remindmelater", true);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    /**
     * Rate app on play store
     */
    public void rate() {
        Uri uri = Uri.parse("market://details?id=" + APP_PACKAGE);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + APP_PACKAGE)));
        }
    }
}

