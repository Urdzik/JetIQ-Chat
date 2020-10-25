package com.example.jetiq_chat.utils

interface Callback<T> {
    fun onSuccess(body: T)
    fun onError(reason: String? = null)
}