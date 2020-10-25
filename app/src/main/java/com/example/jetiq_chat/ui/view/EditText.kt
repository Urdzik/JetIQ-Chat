package com.example.jetiq_chat.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import com.example.jetiq_chat.utils.GuiUtils
import com.google.android.material.textfield.TextInputEditText
import com.example.jetiq_chat.repository.validation.ErrorPresenter


class EditText : TextInputEditText, ErrorPresenter {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
       /* setOnEditorActionListener { _, action , _ ->
            if(action== EditorInfo.IME_ACTION_DONE){
                GuiUtils.hideSoftInput(this)
                return@setOnEditorActionListener true
            }
            false
        }*/
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus()
        }
        return super.onKeyPreIme(keyCode, event)
    }

    override fun setError(error: CharSequence, removeOnChange: Boolean) {
        GuiUtils.setError(this, error.toString(), removeOnChange)
    }
}
