package vn.icheck.android.base.dialog.notify.loading

import android.content.Context
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog

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