package vn.icheck.android.loyalty.dialog.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout

abstract class BaseDialog(context: Context, theme: Int) : Dialog(context, theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getLayoutID)
        window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        setCancelable(getIsCancelable)
        setCanceledOnTouchOutside(getIsCancelable)

        onInitView()
    }

    protected abstract val getLayoutID: Int
    protected abstract val getIsCancelable: Boolean
    protected abstract fun onInitView()
}