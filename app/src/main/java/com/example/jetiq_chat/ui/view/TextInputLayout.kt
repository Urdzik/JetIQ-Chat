package com.example.jetiq_chat.ui.view

import android.content.Context
import android.util.AttributeSet


class TextInputLayout : com.google.android.material.textfield.TextInputLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context, attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
    }
}
