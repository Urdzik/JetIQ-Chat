package com.example.jetiq_chat.ui.view

import android.text.Editable
import android.text.TextWatcher

/**
 * Base text watcher to remove boilerplate code.
 */
open class BaseTextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {}
}
