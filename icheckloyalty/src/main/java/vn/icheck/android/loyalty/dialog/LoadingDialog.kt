package vn.icheck.android.loyalty.dialog

import android.content.Context
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

abstract class LoadingDialog(context: Context) : BaseDialog(context, R.style.DialogThemeTransparent) {

    override val getLayoutID: Int
        get() = R.layout.dialog_loading

    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        setOnDismissListener {
            onDismiss()
        }
    }

    protected abstract fun onDismiss()
}