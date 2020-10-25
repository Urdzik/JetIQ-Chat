package com.example.jetiq_chat.utils

import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import java.lang.ref.WeakReference
import android.view.ViewTreeObserver
import com.example.jetiq_chat.R
import com.example.jetiq_chat.ui.view.BaseTextWatcher


/** GUI utils.  */
object GuiUtils {

    /** Default click handling timeout.  */
    private const val DEFAULT_CLICK_TIMEOUT: Long = 500

    /** Logging tag.  */
    private const val TAG = "GUI"

    /** Default timeout.  */
    val DEFAULT_TIMEOUT: Long = 200

    /**
     * @param textView text view to get text from
     * @return trimmed string
     */
    fun getViewText(textView: TextView): String {
        return textView.text.toString().trim { it <= ' ' }
    }

    /**
     * Hide soft keyboard.
     *
     * @param view view containing current window token
     */
    fun hideSoftInput(view: View) {
        try {
            val imm = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            Log.w(TAG, "Ignore exception", e)
        }

    }

    /**
     * Set error stateType for a text view.
     *
     * @param view         text view
     * @param errorText    error text
     * @param removeOnType whether to remove error on typing
     */
    fun setError(view: TextView?, errorText: CharSequence, removeOnType: Boolean) {
        if (view != null) {
            val parent = searchParent(view, TextInputLayout::class.java)
            if (parent != null) {
                val oldErrorMessage = parent.error
                if (TextUtils.equals(oldErrorMessage, errorText)) {
                    // do nothing to avoid blinks
                    return
                }
                parent.isErrorEnabled = true
                parent.error = errorText
            } else {
                val oldErrorMessage = view.error
                if (TextUtils.equals(oldErrorMessage, errorText)) {
                    // do nothing to avoid blinks
                    return
                }
                view.error = errorText
            }
            if (removeOnType) {
                view.addTextChangedListener(ErrorRemovingTextWatcher(view))
            }
        }
    }

    fun onViewBecomeVisible(view: View, runnable: Runnable) {
        view.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    runnable.run()
                }
            })
    }

    /**
     * Searches view tree for parent of specified type.
     *
     * @param v      view to look parent
     * @param target parent type
     * @param <T>    parent type
     * @return parent view of `null` if parent of specified type doesn't exist at view tree
    </T> */
    private fun <T : View> searchParent(v: View, target: Class<T>): T? {
        var p: ViewParent? = v.parent
        while (p != null) {
            if (target.isAssignableFrom(p.javaClass)) {

                return p as T?
            }
            p = p.parent
        }
        return null
    }

    /** [android.text.TextWatcher] which removes error and self from the view on any change.  */
    private class ErrorRemovingTextWatcher internal constructor(
        /** View to remove error from.  */
        private val view: TextView
    ) : BaseTextWatcher() {

        override fun afterTextChanged(s: Editable) {
            // Do not remove TextWatcher on this event - TextView is iterating over them
            // and removing might cause pesky IndexOutOfBoundsException.
            // See http://stackoverflow.com/questions/7499653/fatal-error-after-text-change-listener-executed
            view.post(object : ViewTask<TextView>(view) {
                override fun execute(v: TextView) {
                    val parent = searchParent(view, TextInputLayout::class.java)
                    if (parent != null) {
                        parent.error = null
                        parent.isErrorEnabled = false
                    } else {
                        v.error = null
                    }
                    v.removeTextChangedListener(this@ErrorRemovingTextWatcher)
                }
            })
        }
    }

    /**
     * Task to manipulate view.
     *
     * @param <V> view type
    </V> */
    private abstract class ViewTask<V : View> constructor(view: V) : Runnable {

        /**
         * View reference.
         */
        private val reference: WeakReference<V> = WeakReference(view)

        override fun run() {
            val view = reference.get()
            if (view != null) {
                execute(view)
            }
        }

        /**
         * Executes operation on view.
         *
         * @param view view to execute operation on
         */
        protected abstract fun execute(view: V)
    }


    /**
     * Disables and re-enabled view after timeout.
     *
     * @param view view to manipulate
     */
    fun debounceView(view: View) {
        debounceView(view, DEFAULT_CLICK_TIMEOUT)
    }

    /**
     * Disables and re-enabled view after timeout.
     *
     * @param view view to manipulate
     * @param timeout disable timeout
     */
    private fun debounceView(view: View?, timeout: Long) {
        if (view is ViewGroup) {
            val viewGroup = view as ViewGroup?
            for (i in 0 until viewGroup!!.childCount) {
                debounceView(viewGroup.getChildAt(i), timeout)
            }
        }
        if (view != null) {
            val wasClickable = view.isClickable
            view.isClickable = false
            view.postDelayed({ view.isClickable = wasClickable }, timeout)
        }
    }

    fun setLinkSpanText(
        textView: TextView, @StringRes prefixTextRes: Int,
        @StringRes clickableTextRes: Int, span: ClickableWithoutUnderlineSpan
    ) {
        textView.isSaveEnabled = false
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = ContextCompat.getColor(textView.context, android.R.color.transparent)
        textView.text = formatLinkSpanText(textView.context, prefixTextRes, clickableTextRes, span)
    }

    fun setLinkSpanText(
        textView: TextView, @StringRes prefixTextRes: Int,
        @StringRes clickableTextRes: Int, action: Runnable
    ) {
        val color = ContextCompat.getColor(textView.context, R.color.black)
        setLinkSpanText(
            textView, prefixTextRes, clickableTextRes,
            ClickSpan(action, color)
        )
    }

    private fun formatLinkSpanText(
        context: Context,
        @StringRes prefixTextRes: Int, @StringRes clickableTextRes: Int,
        span: ClickableWithoutUnderlineSpan
    ): SpannableStringBuilder {
        return formatLinkSpanText(
            context, context.getString(prefixTextRes),
            context.getString(clickableTextRes), span
        )
    }

    private fun formatLinkSpanText(
        context: Context,
        prefixText: String, clickableText: String,
        span: ClickableWithoutUnderlineSpan
    ): SpannableStringBuilder {
        val sb = SpannableStringBuilder(prefixText).append(" ")
        val spanStart = sb.length
        sb.append(clickableText)
        sb.setSpan(span, spanStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    /** Implementation of clickable span without underline with custom action.  */
    class ClickSpan(
        /** Intent builder to use on click.  */
        private val action: Runnable,
        @ColorInt textColor: Int
    ) : ClickableWithoutUnderlineSpan(textColor) {

        override fun onClick(widget: View) {
            debounceView(widget)
            action.run()
        }
    }

    /** Helper class to use clickable span without underline.  */
    abstract class ClickableWithoutUnderlineSpan(
        /** Color for text inside span.  */
        @param:ColorInt @field:ColorInt
        private val textColor: Int
    ) : ClickableSpan() {

        override fun updateDrawState(ds: TextPaint) {
            ds.color = textColor
            ds.isUnderlineText = false
        }
    }

    fun getColor(restId: Int, context: Context): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(restId, typedValue, true)
        return typedValue.data
    }
}
