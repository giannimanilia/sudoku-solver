package com.gmaniliapp.sudokusolver.model

data class Result<out T>(val status: Status, val data: T) {

    companion object {
        fun <T> success(data: T): Result<T> {
            return Result(Status.SUCCESS, data)
        }

        fun <T> error(data: T): Result<T> {
            return Result(Status.ERROR, data)
        }

        fun <T> loading(data: T): Result<T> {
            return Result(Status.LOADING, data)
        }
    }
}