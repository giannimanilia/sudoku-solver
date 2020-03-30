package com.gmaniliapp.sudokusolver.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.gmaniliapp.sudokusolver.R;
import com.gmaniliapp.sudokusolver.application.App;
import com.gmaniliapp.sudokusolver.fragments.SudokuFragment;
import com.gmaniliapp.sudokusolver.utils.AppRater;
import com.gmaniliapp.sudokusolver.utils.SudokuListener;

public class MainActivity extends AppCompatActivity implements SudokuListener {

    private AppRater appRater;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fm_sudoku, new SudokuFragment());
            fragmentTransaction.commit();
        }

        appRater = new AppRater(this);

        progress_bar = findViewById(R.id.progress_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Rate
            case R.id.action_rate:
                appRater.rate();
                return true;
            // Share
            case R.id.action_share:
                App.getInstance().share(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Hide or show progress bar
     */
    public void manageProgress() {
        if (progress_bar.getVisibility() == View.VISIBLE)
            progress_bar.setVisibility(View.INVISIBLE);
        else
            progress_bar.setVisibility(View.VISIBLE);
    }

    /**
     * Display a snackbar
     * @param message: Message to display
     */
    public void displaySnackbar(String message) {
        App.getInstance().displaySnackbar(findViewById(android.R.id.content), message);
    }
}
