package vn.icheck.android.base.fragment

import android.content.Context

interface BaseFragmentView {

    fun showError(errorMessage: String)
    val mContext: Context?
}