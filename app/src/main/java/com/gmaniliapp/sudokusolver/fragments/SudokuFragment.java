package com.gmaniliapp.sudokusolver.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gmaniliapp.sudokusolver.R;
import com.gmaniliapp.sudokusolver.models.Cell;
import com.gmaniliapp.sudokusolver.utils.SudokuListener;

import java.lang.ref.WeakReference;

import static com.gmaniliapp.sudokusolver.utils.Constants.ANIMATION_DURATION;
import static com.gmaniliapp.sudokusolver.utils.Constants.SUDOKU_DIMENSION;
import static com.gmaniliapp.sudokusolver.utils.Constants.SUDOKU_ORDER;

public class SudokuFragment extends Fragment {

    private SudokuListener sudokuListener;
    private FragmentActivity fragmentActivity;
    private SudokuTask sudokuTask;
    private Cell[][] board;
    private TableLayout tl_sudoku;
    private Button bt_solve;
    private Button bt_reset;

    public SudokuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SudokuListener){
            this.sudokuListener = (SudokuListener) context;
        }
        else {
            throw new ClassCastException();
        }

        if (context instanceof Activity) {
            this.fragmentActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        board = createEmptyBoard();
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
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

        tl_sudoku = view.findViewById(R.id.tl_sudoku);
        for (int i = 0; i < SUDOKU_DIMENSION; i++) {
            // Create new row
            TableRow tr_row = new TableRow(fragmentActivity);
            tr_row.setLayoutParams(tableParams);

            for (int j = 0; j < SUDOKU_DIMENSION; j++) {
                final int finalI = i;
                final int finalJ = j;

                // Create new cell
                final EditText et_number = new EditText(fragmentActivity);
                et_number.setLayoutParams(cellParams);
                et_number.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                et_number.setBackground(fragmentActivity.getResources().getDrawable(getBackground(i, j)));
                et_number.setKeyListener(DigitsKeyListener.getInstance("123456789"));

                InputFilter[] inputFilters = new InputFilter[1];
                inputFilters[0] = new InputFilter.LengthFilter(1);
                et_number.setFilters(inputFilters);

                et_number.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String value = editable.toString();

                        if (board[finalI][finalJ].getValue() == 0) {
                            if (!value.isEmpty()) {
                                board[finalI][finalJ].setHighlighted(true);
                                et_number.setTypeface(null, Typeface.BOLD);
                            }
                        }
                        else {
                            board[finalI][finalJ].setHighlighted(false);
                            et_number.setTypeface(null, Typeface.NORMAL);
                        }

                        if (value.isEmpty())
                            board[finalI][finalJ].setValue(0);
                        else
                            board[finalI][finalJ].setValue(Integer.parseInt(value));
                    }
                });
                tr_row.addView(et_number);
            }
            tl_sudoku.addView(tr_row);
        }

        loadBoard();

        // Configure clear
        Button bt_clear = view.findViewById(R.id.bt_clear);
        bt_clear.setOnClickListener(view12 -> {
            stopIfRunning();
            board = createEmptyBoard();
            setSolvedStatus(false);
            loadBoard();
        });

        // Configure solve
        bt_solve = view.findViewById(R.id.bt_solve);
        bt_solve.setOnClickListener(view1 -> {
            if (!stopIfRunning()) {
                sudokuTask = new SudokuTask(SudokuFragment.this);
                sudokuTask.execute();
            }
        });

        // Configure reset
        bt_reset = view.findViewById(R.id.bt_reset);
        bt_reset.setOnClickListener(view13 -> {
            stopIfRunning();
            resetBoard(board);
            setSolvedStatus(false);
            loadBoard();
        });
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
     * Load board
     */
    private void loadBoard() {
        for (int i = 0; i < tl_sudoku.getChildCount(); i++) {
            TableRow tr_row = (TableRow) tl_sudoku.getChildAt(i);
            for (int j = 0; j < tr_row.getChildCount(); j++) {
                EditText et_number = (EditText) tr_row.getChildAt(j);
                et_number.setBackground(fragmentActivity.getResources().getDrawable(getBackground(i, j)));

                if (board[i][j].isHighlighted())
                    continue;
                else if (board[i][j].getValue() == 0)
                    et_number.setText("");
                else
                    et_number.setText(String.valueOf(board[i][j].getValue()));

                et_number.startAnimation(getAnimation());
            }
        }
    }

    /**
     * Set board's solved status
     * @param solved: Status to set
     */
    private void setSolvedStatus(boolean solved) {
        bt_solve.setText(fragmentActivity.getString(R.string.solve));
        bt_solve.setEnabled(!solved);
        bt_reset.setEnabled(solved);
    }

    /**
     * Stop task if running
     * @return boolean
     */
    private boolean stopIfRunning() {
        if (sudokuTask != null && sudokuTask.getStatus() == AsyncTask.Status.RUNNING) {
            sudokuTask.cancel(true);
            return true;
        }
        return false;
    }

    /**
     * Reset board to just highlighted cells
     * @param board: Board to reset
     */
    private void resetBoard(Cell[][] board) {
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                if (!cell.isHighlighted())
                    cell.setValue(0);
            }
        }
    }

    /**
     * Create an empty board
     * @return Cell[][]
     */
    private Cell[][] createEmptyBoard() {
        Cell[][] current = new Cell[SUDOKU_DIMENSION][SUDOKU_DIMENSION];
        for (int i = 0; i < SUDOKU_DIMENSION; i++) {
            for (int j = 0; j < SUDOKU_DIMENSION; j++) {
                current[i][j] = new Cell(0, false);
            }
        }
        return current;
    }

    /**
     * Check if board if full of 0
     * @param board: Board to check
     * @return boolean
     */
    private boolean isBoardEmpty(Cell[][] board) {
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                if (cell.getValue() != 0)
                    return false;
            }
        }
        return true;
    }

    /**
     * Get board's animation
     * @return Animation
     */
    private Animation getAnimation() {
        Animation animation = AnimationUtils.loadAnimation(fragmentActivity, android.R.anim.slide_in_left);
        animation.setDuration(ANIMATION_DURATION);
        return animation;
    }

    /**
     * Task to solving's management
     */
    private static class SudokuTask extends AsyncTask<Void, Void, Boolean> {
        private SudokuFragment fragment;

        private SudokuTask(SudokuFragment sudokuFragment) {
            WeakReference<SudokuFragment> weakReference = new WeakReference<>(sudokuFragment);
            this.fragment = weakReference.get();
        }

        @Override
        protected void onPreExecute() {
            if (fragment == null || fragment.fragmentActivity.isFinishing())
                cancel(true);

            fragment.sudokuListener.manageProgress();

            if (fragment.isBoardEmpty(fragment.board)) {
                fragment.sudokuListener.displaySnackbar(fragment.fragmentActivity.getString(R.string.empty_sudoku));
                cancel(true);
            }
            else {
                fragment.bt_solve.setText(fragment.fragmentActivity.getString(R.string.stop));
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (fragment == null || fragment.fragmentActivity.isFinishing())
                cancel(true);

            return solveSudoku(fragment.board);
        }

        @Override
        protected void onPostExecute(Boolean solved) {
            if (fragment == null || fragment.fragmentActivity.isFinishing())
                cancel(true);

            if (!solved)
                fragment.sudokuListener.displaySnackbar(fragment.fragmentActivity.getString(R.string.unsolvable_sudoku));
            else
                fragment.loadBoard();

            fragment.sudokuListener.manageProgress();
            fragment.setSolvedStatus(true);
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            fragment.sudokuListener.manageProgress();
            fragment.setSolvedStatus(false);
        }

        /**
         * Solve sudoku using backtracking
         * @param sudoku: Sudoku to solve
         * @return boolean
         */
        private boolean solveSudoku(Cell[][] sudoku) {
            if (isCancelled())
                return false;

            for (int row = 0; row < SUDOKU_DIMENSION; row ++) {
                for (int column = 0; column < SUDOKU_DIMENSION; column++) {
                    if (sudoku[row][column].getValue() == 0) {
                        for (int number = 1; number <= SUDOKU_DIMENSION; number++) {
                            if (!isInRow(row, number, sudoku) && !isInColumn(column, number, sudoku) && !isInBox(row, column, number, sudoku)) {
                                sudoku[row][column].setValue(number);
                                if (solveSudoku(sudoku)) {
                                    return true;
                                }
                                else {
                                    sudoku[row][column].setValue(0);
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
         * @param sudoku: Sudoku to solve
         * @return boolean
         */
        private boolean isInRow(int row, int number, Cell[][] sudoku) {
            for (int column = 0; column < SUDOKU_DIMENSION; column++) {
                if (sudoku[row][column].getValue() == number)
                    return true;
            }
            return false;
        }

        /**
         * Check if number is already present in column
         * @param column: Number's column
         * @param number: Number to check
         * @param sudoku: Sudoku to solve
         * @return boolean
         */
        private boolean isInColumn(int column, int number, Cell[][] sudoku) {
            for (int row = 0; row < SUDOKU_DIMENSION; row++) {
                if (sudoku[row][column].getValue() == number)
                    return true;
            }

            return false;
        }

        /**
         * Check if number is already present in
         * @param row: Number's row
         * @param column: Number's column
         * @param number: Number to check
         * @param sudoku: Sudoku to solve
         * @return boolean
         */
        private boolean isInBox(int row, int column, int number, Cell[][] sudoku) {
            int initialRow = row - row % SUDOKU_ORDER;
            int initialColumn = column - column % SUDOKU_ORDER;

            for (int i = initialRow; i < initialRow + SUDOKU_ORDER; i++) {
                for (int j = initialColumn; j < initialColumn + SUDOKU_ORDER; j++) {
                    if (sudoku[i][j].getValue() == number)
                        return true;
                }
            }
            return false;
        }
    }
}
