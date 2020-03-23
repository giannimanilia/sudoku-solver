package com.gmaniliapp.sudokusolver.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gmaniliapp.sudokusolver.R;
import com.gmaniliapp.sudokusolver.activities.MainActivity;
import com.gmaniliapp.sudokusolver.application.App;

import static com.gmaniliapp.sudokusolver.utils.Constants.SUDOKU_DIMENSION;
import static com.gmaniliapp.sudokusolver.utils.Constants.SUDOKU_ORDER;

public class SudokuFragment extends Fragment {

    private FragmentActivity fragmentActivity;
    private int[][] sudoku;
    private int[][] original;
    private TableLayout tl_sudoku;

    public SudokuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity){
            this.fragmentActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // sudoku = new int[SUDOKU_DIMENSION][SUDOKU_DIMENSION];
        sudoku = createTestSudoku();
        original = new int[SUDOKU_DIMENSION][SUDOKU_DIMENSION];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sudoku, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareLayout(view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.fragmentActivity = null;
    }

    private void prepareLayout(View view) {
        // Populate table dynamically
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        tl_sudoku = view.findViewById(R.id.tl_sudoku);
        for (int i = 0; i < SUDOKU_DIMENSION; i++) {
            TableRow tr_row = new TableRow(fragmentActivity);
            tr_row.setLayoutParams(tableParams);

            for (int j = 0; j < SUDOKU_DIMENSION; j++) {
                final int finalI = i;
                final int finalJ = j;

                final EditText et_number = new EditText(fragmentActivity);
                et_number.setLayoutParams(rowParams);
                et_number.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                et_number.setBackground(fragmentActivity.getResources().getDrawable(getBackground(i, j)));
                et_number.setTypeface(null, Typeface.BOLD);
                et_number.setKeyListener(DigitsKeyListener.getInstance("123456789"));
                if (sudoku[i][j] != 0) {
                    et_number.setText(String.valueOf(sudoku[i][j]));
                } else {
                    et_number.setText("");
                }
                et_number.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.toString().isEmpty())
                            sudoku[finalI][finalJ] = 0;
                        else
                            sudoku[finalI][finalJ] = Integer.parseInt(editable.toString());

                        et_number.setTypeface(null, Typeface.BOLD);
                    }
                });

                tr_row.addView(et_number);
            }
            tl_sudoku.addView(tr_row);

            // Configure reset
            Button bt_reset = view.findViewById(R.id.bt_reset);
            bt_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sudoku = new int[SUDOKU_DIMENSION][SUDOKU_DIMENSION];
                    original = new int[SUDOKU_DIMENSION][SUDOKU_DIMENSION];
                    updateBoard();
                }
            });

            // Configure solve
            Button bt_solve = view.findViewById(R.id.bt_solve);
            bt_solve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEmptyArray(sudoku)) {
                        App.getInstance().displaySnackbar(fragmentActivity.findViewById(android.R.id.content), fragmentActivity.getString(R.string.empty_sudoku));
                        return;
                    }

                    original = cloneArray(sudoku);
                    if (solveSudoku(sudoku)) {
                        updateBoard();
                    } else {
                        App.getInstance().displaySnackbar(fragmentActivity.findViewById(android.R.id.content), fragmentActivity.getString(R.string.unsolvable_sudoku));
                    }
                }
            });
        }
    }

    /**
     * Select background depending of position
     * @param row: Element's row
     * @param column: Element's column
     * @return int
     */
    private int getBackground(int row, int column) {
        if (column % SUDOKU_ORDER == 0 && row % SUDOKU_ORDER == 0)
            return R.drawable.grid_top_left_border;
        else if (column % SUDOKU_ORDER == 0 && row == SUDOKU_DIMENSION - 1)
            return R.drawable.grid_bottom_left_border;
        else if (column == SUDOKU_DIMENSION - 1 && row % SUDOKU_ORDER == 0)
            return R.drawable.grid_top_right_border;
        else if (column == SUDOKU_DIMENSION - 1 && row == SUDOKU_DIMENSION - 1)
            return R.drawable.grid_bottom_right_border;
        else if (column == SUDOKU_DIMENSION - 1)
            return R.drawable.grid_right_border;
        else if (column % SUDOKU_ORDER != 0 && row == SUDOKU_DIMENSION - 1)
            return R.drawable.grid_bottom_border;
        else if (column % SUDOKU_ORDER == 0)
            return R.drawable.grid_left_border;
        else if (row % SUDOKU_ORDER == 0)
            return R.drawable.grid_top_border;
        else
            return R.drawable.grid_border;
    }

    /**
     * Update board with results
     */
    private void updateBoard() {
        for (int i = 0; i < tl_sudoku.getChildCount(); i++) {
            TableRow tr_row = (TableRow) tl_sudoku.getChildAt(i);
            for (int j = 0; j < tr_row.getChildCount(); j++) {
                EditText et_number = (EditText) tr_row.getChildAt(j);
                et_number.setBackground(fragmentActivity.getResources().getDrawable(getBackground(i, j)));
                if (sudoku[i][j] != 0 && sudoku[i][j] != original[i][j]) {
                    et_number.setText(String.valueOf(sudoku[i][j]));
                    et_number.setTypeface(null, Typeface.NORMAL);
                }
                else if (sudoku[i][j] == 0) {
                    et_number.setText("");
                }
            }
        }
    }

    /**
     * Solve sudoku using backtracking
     * @param sudoku: Sudoku to solve
     * @return int[][]
     */
    private boolean solveSudoku(int[][] sudoku) {
        for (int row = 0; row < SUDOKU_DIMENSION; row ++) {
            for (int column = 0; column < SUDOKU_DIMENSION; column++) {
                if (sudoku[row][column] == 0) {
                    for (int number = 1; number <= SUDOKU_DIMENSION; number++) {
                        if (!isInRow(row, number) && !isInColumn(column, number) && !isInBox(row, column, number)) {
                            sudoku[row][column] = number;
                            if (solveSudoku(sudoku)) {
                                return true;
                            }
                            else {
                                sudoku[row][column] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if number is already present in row
     * @param row: Number's row
     * @param number: Number to check
     * @return boolean
     */
    private boolean isInRow(int row, int number) {
        for (int column = 0; column < SUDOKU_DIMENSION; column++) {
            if (sudoku[row][column] == number)
                return true;
        }
        return false;
    }

    /**
     * Check if number is already present in column
     * @param column: Number's column
     * @param number: Number to check
     * @return boolean
     */
    private boolean isInColumn(int column, int number) {
        for (int row = 0; row < SUDOKU_DIMENSION; row++) {
            if (sudoku[row][column] == number)
                return true;
        }

        return false;
    }

    /**
     * Check if number is already present in
     * @param row: Number's row
     * @param column: Number's column
     * @param number: Number to check
     * @return boolean
     */
    private boolean isInBox(int row, int column, int number) {
        int initialRow = row - row % SUDOKU_ORDER;
        int initialColumn = column - column % SUDOKU_ORDER;

        for (int i = initialRow; i < initialRow + SUDOKU_ORDER; i++) {
            for (int j = initialColumn; j < initialColumn + SUDOKU_ORDER; j++) {
                if (sudoku[i][j] == number)
                    return true;
            }
        }
        return false;
    }

    /**
     * Create sudoku for test
     * @return int[][]
     */
    private int[][] createTestSudoku() {
        return new int[][]{
            {0,0,0,2,6,0,7,0,0},
            {6,8,0,0,7,0,0,9,0},
            {1,9,0,0,0,4,5,0,0},
            {8,2,0,1,0,0,0,4,0},
            {0,0,4,6,0,2,9,0,0},
            {0,5,0,0,0,3,0,2,8},
            {0,0,9,3,0,0,0,7,4},
            {0,4,0,0,5,0,0,3,6},
            {7,0,3,0,1,8,0,0,0},
        };
    }

    /**
     * Clone multidimensional array
     * @param old: Array to clone
     * @return int[][]
     */
    private int[][] cloneArray(int[][] old) {
        int[][] current = new int[old.length][old[0].length];
        for (int i = 0; i < old.length; i++) {
            System.arraycopy(old[i], 0, current[i], 0, old[i].length);
        }
        return current;
    }

    private boolean isEmptyArray(int[][] values) {
        for (int[] value : values) {
            for (int i : value) {
                if (i != 0)
                    return false;
            }
        }
        return true;
    }
}
