package com.example.jetiq_chat.repository.validation

/** Allows to display an error message for views that implement this interface.  */
interface ErrorPresenter {
    fun setError(error: CharSequence, removeOnChange: Boolean)
}