package vn.icheck.android.util

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import vn.icheck.android.helper.RelationshipHelper
import vn.icheck.android.constant.*
import vn.icheck.android.network.base.SessionManager

fun createViewFragment(inflater: LayoutInflater, container: ViewGroup?, @LayoutRes layoutId: Int): View {
    return inflater.inflate(layoutId, container, false)
}

/**
 * Method + chuoi
 * string > string
 */
fun SpannableStringBuilder.spansAppend(
        text: CharSequence,
        flags: Int,
        vararg spans: Any
): SpannableStringBuilder {
    val start = length
    append(text)

    spans.forEach { span ->
        setSpan(span, start, length, flags)
    }

    return this
}

fun isValidEmail(string: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(string).matches() && string.isNotEmpty()
}

fun hideKeyboard(activity: FragmentActivity?, view: View?) {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}



