package vn.icheck.android.base.activity

import android.content.Context

interface BaseActivityView {

    fun showError(errorMessage: String)
    val mContext: Context
    fun onShowLoading(isShow: Boolean)
}