package com.fjrh.FabrikApp.domain.result

import com.fjrh.FabrikApp.domain.exception.AppException

/**
 * Resultado de operaciones que puede ser éxito o error
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: AppException) : Result<Nothing>()
    object Loading : Result<Nothing>()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }

    fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }

    fun onError(action: (AppException) -> Unit): Result<T> {
        if (this is Error) {
            action(exception)
        }
        return this
    }

    fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) {
            action()
        }
        return this
    }
}

/**
 * Extensiones útiles para Result
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> Result.Error(exception)
    is Result.Loading -> Result.Loading
}

inline fun <T> Result<T>.mapError(transform: (AppException) -> AppException): Result<T> = when (this) {
    is Result.Success -> this
    is Result.Error -> Result.Error(transform(exception))
    is Result.Loading -> this
}

inline fun <T> Result<T>.flatMap(transform: (T) -> Result<T>): Result<T> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> Result.Error(exception)
    is Result.Loading -> Result.Loading
}

