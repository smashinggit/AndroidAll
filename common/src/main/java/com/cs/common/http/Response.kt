package com.cs.common.http

data class Response<T>(
    var state: State,
    val data: T?,
    var message: String?
) {

    companion object {

        fun <T> loading(tip: String = "加载中..."): Response<T> {
            return Response(State.LOADING, null, tip)
        }

        fun <T> loading(tip: String = "加载中...", data: T): Response<T> {
            return Response(State.LOADING, data, tip)
        }

        fun <T> success(data: T): Response<T> {
            return Response(State.SUCCESS, data, null)
        }

        fun <T> error(msg: String): Response<T> {
            return Response(State.ERROR, null, msg)
        }
    }
}

enum class State {
    LOADING, SUCCESS, ERROR
}