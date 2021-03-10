package vn.icheck.android.util

import android.text.Editable
import android.text.TextWatcher

abstract class AfterTextWatcher: TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not implement
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not implement
    }
}