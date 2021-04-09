package vn.icheck.android.ichecklibs.base_dialog

import android.content.Context
import vn.icheck.android.ichecklibs.R

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