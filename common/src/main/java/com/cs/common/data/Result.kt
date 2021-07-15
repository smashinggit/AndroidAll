package com.cs.common.data

/**
 * @author ChenSen
 * @since 2021/7/15 16:11
 * @desc
 */
sealed class Result<out T : Any> {

    data class SUCCESS<out T : Any>(val data: T) : Result<T>()

    data class LOADING<out T : Any>(val msg: String) : Result<T>()

    data class FAILURE<out T : Any>(val code: Int, val msg: String) : Result<T>()

    data class ERROR<out T : Any>(val code: Int, val msg: String) : Result<T>()
}