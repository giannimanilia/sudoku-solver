package com.gmaniliapp.sudokusolver.ui.sudoku

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmaniliapp.sudokusolver.common.Constants
import com.gmaniliapp.sudokusolver.exception.IllegalSudokuContentException
import com.gmaniliapp.sudokusolver.exception.IllegalSudokuDimensionException
import com.gmaniliapp.sudokusolver.model.Cell
import com.gmaniliapp.sudokusolver.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SudokuViewModel : ViewModel() {

    private val _solvingStatus = MutableLiveData<Result<String>>()
    val solvingStatus: LiveData<Result<String>> = _solvingStatus

    fun validateAndSolveSudoku(sudoku: Array<Array<Cell?>>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                validateSudoku(sudoku)
                solveSudoku(sudoku)
            } catch (exception: IllegalSudokuDimensionException) {
                _solvingStatus.postValue(Result.error(exception.message))
            } catch (exception: IllegalSudokuContentException) {
                _solvingStatus.postValue(Result.error(exception.message))
            }
        }
    }

    private fun solveSudoku(sudoku: Array<Array<Cell?>>) {
        _solvingStatus.postValue(Result.loading("Solving the sudoku"))

        val solved = isSudokuSolvable(sudoku)
        if (solved) {
            _solvingStatus.postValue(Result.success("Sudoku solved"))
        } else {
            _solvingStatus.postValue(Result.error("Sudoku can't be solved"))
        }
    }

    private fun validateSudoku(sudoku: Array<Array<Cell?>>) {
        validateSudokuNotEmpty(sudoku)
        validateSudokuDimension(sudoku)
        validateSudokuContent(sudoku)
    }

    private fun validateSudokuNotEmpty(sudoku: Array<Array<Cell?>>) {
        if (isSudokuEmpty(sudoku)) {
            throw IllegalSudokuContentException("Sudoku can't be empty")
        }
    }

    private fun isSudokuEmpty(sudoku: Array<Array<Cell?>>): Boolean {
        for (cells in sudoku) {
            for (cell in cells) {
                if (cell!!.value != 0) {
                    return false
                }
            }
        }
        return true
    }

    private fun validateSudokuDimension(sudoku: Array<Array<Cell?>>) {
        if (!isValidSudokuDimension(sudoku)) {
            throw IllegalSudokuDimensionException("Sudoku dimension don't match. Required dimension = " + Constants.SUDOKU_DIMENSION + ". Sudoku's dimension = " + sudoku.size)
        }
    }

    private fun isValidSudokuDimension(sudoku: Array<Array<Cell?>>): Boolean {
        return sudoku.size == Constants.SUDOKU_DIMENSION && sudoku[0].size == Constants.SUDOKU_DIMENSION
    }

    private fun validateSudokuContent(sudoku: Array<Array<Cell?>>) {
        if (!isValidSudokuContent(sudoku)) {
            throw IllegalSudokuContentException("Sudoku's can't be solved")
        }
    }

    private fun isValidSudokuContent(sudoku: Array<Array<Cell?>>): Boolean {
        for (row in 0 until Constants.SUDOKU_DIMENSION) {
            for (column in 0 until Constants.SUDOKU_DIMENSION) {
                val number = sudoku[row][column]!!.value
                if (number != 0) {
                    sudoku[row][column]!!.value = 0
                    if (isNumberPresent(number, row, column, sudoku)) {
                        sudoku[row][column]!!.value = number
                        return false
                    } else {
                        sudoku[row][column]!!.value = number
                    }
                }
            }
        }
        return true
    }

    private fun isSudokuSolvable(sudoku: Array<Array<Cell?>>): Boolean {
        for (row in 0 until Constants.SUDOKU_DIMENSION) {
            for (column in 0 until Constants.SUDOKU_DIMENSION) {
                if (sudoku[row][column]!!.value == 0) {
                    for (number in 1..Constants.SUDOKU_DIMENSION) {
                        if (isNumberNotPresent(number, row, column, sudoku)) {
                            sudoku[row][column]!!.value = number
                            if (isSudokuSolvable(sudoku)) {
                                return true
                            } else {
                                sudoku[row][column]!!.value = 0
                            }
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isNumberPresent(number: Int, row: Int, column: Int, sudoku: Array<Array<Cell?>>): Boolean {
        return isNumberInRow(number, row, sudoku) || isNumberInColumn(number, column, sudoku) || isNumberInBox(number, row, column, sudoku)
    }

    private fun isNumberNotPresent(number: Int, row: Int, column: Int, sudoku: Array<Array<Cell?>>): Boolean {
        return !isNumberPresent(number, row, column, sudoku)
    }

    private fun isNumberInRow(number: Int, row: Int, sudoku: Array<Array<Cell?>>): Boolean {
        for (column in 0 until Constants.SUDOKU_DIMENSION) {
            if (sudoku[row][column]!!.value == number) {
                return true
            }
        }
        return false
    }

    private fun isNumberInColumn(number: Int, column: Int, sudoku: Array<Array<Cell?>>): Boolean {
        for (row in 0 until Constants.SUDOKU_DIMENSION) {
            if (sudoku[row][column]!!.value == number) {
                return true
            }
        }
        return false
    }

    private fun isNumberInBox(number: Int, row: Int, column: Int, sudoku: Array<Array<Cell?>>): Boolean {
        val initialRow = row - row % Constants.SUDOKU_ORDER
        val initialColumn = column - column % Constants.SUDOKU_ORDER
        for (i in initialRow until initialRow + Constants.SUDOKU_ORDER) {
            for (j in initialColumn until initialColumn + Constants.SUDOKU_ORDER) {
                if (sudoku[i][j]!!.value == number) {
                    return true
                }
            }
        }
        return false
    }
}