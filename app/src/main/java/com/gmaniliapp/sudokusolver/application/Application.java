package com.gmaniliapp.sudokusolver.application;

import android.content.Context;
import android.content.Intent;

import com.gmaniliapp.sudokusolver.R;

import static com.gmaniliapp.sudokusolver.common.Constants.APP_LINK;

public class Application extends android.app.Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }

    public void shareApp(Context context) {
        String message = getString(R.string.share_message, getString(R.string.app_name), APP_LINK);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }
}
