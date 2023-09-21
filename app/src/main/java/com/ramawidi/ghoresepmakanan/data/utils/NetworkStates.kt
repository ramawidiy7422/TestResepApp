package com.ramawidi.ghoresepmakanan.data.utils

sealed class NetworkStates<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T): NetworkStates<T>(data)

    class Error<T>(message: String?, data: T? = null): NetworkStates<T>(data, message)

    class Loading<T>: NetworkStates<T>()

}