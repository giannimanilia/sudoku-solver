package com.gmaniliapp.sudokusolver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.gmaniliapp.sudokusolver.R;
import com.gmaniliapp.sudokusolver.fragments.SudokuFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fm_sudoku, new SudokuFragment());
            fragmentTransaction.commit();
        }
    }
}
