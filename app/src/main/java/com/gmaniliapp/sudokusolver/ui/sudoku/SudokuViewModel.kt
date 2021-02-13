package com.gmaniliapp.sudokusolver.ui.sudoku

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmaniliapp.sudokusolver.common.Constants
import com.gmaniliapp.sudokusolver.model.Cell
import com.gmaniliapp.sudokusolver.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SudokuViewModel : ViewModel() {

    private val _solvingStatus = MutableLiveData<Result<String>>()
    val solvingStatus: LiveData<Result<String>> = _solvingStatus

    fun validateAndSolveSudoku(sudoku: Array<Array<Cell?>>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isBoardEmpty(sudoku)) {
                _solvingStatus.postValue(Result.error("Sudoku can't be empty"))
            } else if (isValidSudoku(sudoku)) {
                _solvingStatus.postValue(Result.loading("Solving the sudoku"))
                val solved = isSudokuSolvable(sudoku)
                if (!solved) {
                    _solvingStatus.postValue(Result.error("Sudoku can't be solved"))
                } else {
                    _solvingStatus.postValue(Result.success("Sudoku solved"))
                }
            } else {
                _solvingStatus.postValue(Result.error("Invalid sudoku"))
            }
        }
    }

    private fun isBoardEmpty(board: Array<Array<Cell?>>): Boolean {
        for (cells in board) {
            for (cell in cells) {
                if (cell!!.value != 0) {
                    return false
                }
            }
        }
        return true
    }

    private fun isValidSudoku(sudoku: Array<Array<Cell?>>): Boolean {
        if (sudoku.size != Constants.SUDOKU_DIMENSION || sudoku[0].size != Constants.SUDOKU_DIMENSION) {
            return false
        }
        for (row in 0 until Constants.SUDOKU_DIMENSION) {
            for (column in 0 until Constants.SUDOKU_DIMENSION) {
                val number = sudoku[row][column]!!.value
                if (number != 0) {
                    sudoku[row][column]!!.value = 0
                    if (isInRow(row, number, sudoku) || isInColumn(column, number, sudoku) || isInBox(row, column, number, sudoku)) {
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
                        if (!isInRow(row, number, sudoku) && !isInColumn(column, number, sudoku) && !isInBox(row, column, number, sudoku)) {
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

    private fun isInRow(row: Int, number: Int, sudoku: Array<Array<Cell?>>): Boolean {
        for (column in 0 until Constants.SUDOKU_DIMENSION) {
            if (sudoku[row][column]!!.value == number) {
                return true
            }
        }
        return false
    }

    private fun isInColumn(column: Int, number: Int, sudoku: Array<Array<Cell?>>): Boolean {
        for (row in 0 until Constants.SUDOKU_DIMENSION) {
            if (sudoku[row][column]!!.value == number) {
                return true
            }
        }
        return false
    }

    private fun isInBox(row: Int, column: Int, number: Int, sudoku: Array<Array<Cell?>>): Boolean {
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