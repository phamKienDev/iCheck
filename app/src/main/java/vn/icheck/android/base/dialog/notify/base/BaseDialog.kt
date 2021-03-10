package vn.icheck.android.base.dialog.notify.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import vn.icheck.android.R

abstract class BaseDialog(context: Context, theme: Int) : Dialog(context, theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID)
        window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        setCancelable(getIsCancelable)
        setCanceledOnTouchOutside(getIsCancelable)

        onInitView()
    }

    protected abstract val getLayoutID: Int
    protected abstract val getIsCancelable: Boolean
    protected abstract fun onInitView()
}