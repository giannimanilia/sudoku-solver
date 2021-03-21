package com.gmaniliapp.sudokusolver.ui.sudoku

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.gmaniliapp.sudokusolver.R
import com.gmaniliapp.sudokusolver.common.Constants
import com.gmaniliapp.sudokusolver.model.Cell
import com.gmaniliapp.sudokusolver.model.Status
import com.gmaniliapp.sudokusolver.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_sudoku.*

class SudokuFragment : BaseFragment(R.layout.fragment_sudoku) {

    private lateinit var board: Array<Array<Cell?>>
    private lateinit var sudokuListener: SudokuListener

    private val viewModel: SudokuViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SudokuListener) {
            sudokuListener = (context)
        } else {
            throw ClassCastException()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        board = createEmptyBoard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        prepareLayout()
    }

    private fun subscribeToObservers() {
        viewModel.solvingStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        displaySnackbar(getStringByName(result.data))
                        loadBoard()
                        sudokuListener.manageProgressBar()
                        setSolvedStatus(true)
                    }
                    Status.ERROR -> {
                        displaySnackbar(getStringByName(result.data))
                        sudokuListener.manageProgressBar()
                        setSolvedStatus(false)
                    }
                    Status.LOADING -> {
                        sudokuListener.manageProgressBar()
                    }
                }
            }
        })
    }

    private fun prepareLayout() {
        populateTable()

        loadBoard()

        configureClearButton()

        configureSolveButton()

        configureResetButton()
    }

    private fun populateTable() {
        val tableParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
        val cellParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)
        val numberParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)

        numberParams.setMargins(8, 8, 8, 8)

        for (i in 0 until Constants.SUDOKU_DIMENSION) {
            val trRow = TableRow(requireActivity())
            trRow.layoutParams = tableParams

            for (j in 0 until Constants.SUDOKU_DIMENSION) {
                val finalI = i

                val inputFilters = arrayOfNulls<InputFilter>(1)
                inputFilters[0] = LengthFilter(1)

                val etNumber = EditText(requireActivity())
                etNumber.layoutParams = numberParams
                etNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.cell_number)
                etNumber.textAlignment = View.TEXT_ALIGNMENT_CENTER
                etNumber.isCursorVisible = false
                etNumber.keyListener = DigitsKeyListener.getInstance("123456789")
                etNumber.setSelectAllOnFocus(true)
                etNumber.highlightColor = ContextCompat.getColor(requireContext(), R.color.transparent)
                etNumber.filters = inputFilters
                etNumber.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

                    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

                    override fun afterTextChanged(editable: Editable) {
                        val value = editable.toString()

                        if (board[finalI][j]!!.value == 0) {
                            // Value inserted by the user
                            if (value.isNotEmpty()) {
                                board[finalI][j]!!.isHighlighted = true
                                etNumber.setTypeface(null, Typeface.BOLD)
                            }
                            etNumber.isActivated = value.isNotEmpty()
                        } else {
                            // Value inserted by the solver
                            board[finalI][j]!!.isHighlighted = false
                            etNumber.setTypeface(null, Typeface.NORMAL)
                            etNumber.isActivated = false
                        }

                        if (value.isEmpty()) {
                            board[finalI][j]!!.value = 0
                        } else {
                            board[finalI][j]!!.value = value.toInt()
                        }
                    }
                })

                val cell = LinearLayout(requireActivity())
                cell.layoutParams = cellParams
                cell.background = ContextCompat.getDrawable(requireContext(), getCellBackground(i, j))
                cell.addView(etNumber)
                trRow.addView(cell)
            }

            tlSudoku.addView(trRow)
        }
    }

    private fun configureClearButton() {
        btClear.setOnClickListener { view12: View? ->
            board = createEmptyBoard()
            setSolvedStatus(false)
            loadBoard()
        }
    }

    private fun configureSolveButton() {
        btSolve.setOnClickListener {
            btSolve.text = getString(R.string.sudoku_action_stop)
            viewModel.validateAndSolveSudoku(board)
        }
    }

    private fun configureResetButton() {
        btReset.setOnClickListener {
            resetBoard(board)
            setSolvedStatus(false)
            loadBoard()
        }
    }

    private fun getCellBackground(row: Int, column: Int): Int {
        return if (column == 0) {
            if (row == 0) R.drawable.cell_neutral else if (row % Constants.SUDOKU_ORDER == 0) R.drawable.cell_neutral_horizontal else R.drawable.cell_horizontal
        } else if (column % Constants.SUDOKU_ORDER == 0) {
            if (row == 0) return R.drawable.cell_neutral_vertical
            if (row % Constants.SUDOKU_ORDER == 0) R.drawable.cell_square_limit else R.drawable.cell_vertical_limit
        } else {
            if (row == 0) R.drawable.cell_vertical else if (row % Constants.SUDOKU_ORDER == 0) R.drawable.cell_horizontal_limit else R.drawable.cell_square
        }
    }

    private fun loadBoard() {
        for (i in 0 until tlSudoku!!.childCount) {
            if (tlSudoku!!.getChildAt(i) !is TableRow) return
            val tr_row = tlSudoku!!.getChildAt(i) as TableRow
            for (j in 0 until tr_row.childCount) {
                if (tr_row.getChildAt(j) !is LinearLayout) return
                val root = tr_row.getChildAt(j) as LinearLayout
                for (k in 0 until root.childCount) {
                    if (root.getChildAt(k) !is EditText) return
                    val et_number = root.getChildAt(k) as EditText
                    if (board[i][j]!!.isHighlighted) continue else if (board[i][j]!!.value == 0) et_number.setText("") else et_number.setText(board[i][j]!!.value.toString())
                }
            }
        }
    }

    private fun setSolvedStatus(solved: Boolean) {
        btSolve!!.text = getString(R.string.sudoku_action_solve)
        btSolve!!.isEnabled = !solved
        btReset!!.isEnabled = solved
    }

    private fun resetBoard(board: Array<Array<Cell?>>) {
        for (cells in board) {
            for (cell in cells) {
                if (!cell!!.isHighlighted) cell.value = 0
            }
        }
    }

    private fun createEmptyBoard(): Array<Array<Cell?>> {
        val current = Array(Constants.SUDOKU_DIMENSION) { arrayOfNulls<Cell>(Constants.SUDOKU_DIMENSION) }
        for (i in 0 until Constants.SUDOKU_DIMENSION) {
            for (j in 0 until Constants.SUDOKU_DIMENSION) {
                current[i][j] = Cell(0, false)
            }
        }
        return current
    }

    private fun getStringByName(name: String?): String {
        return getString(resources.getIdentifier(name, "string", requireActivity().packageName))
    }
}