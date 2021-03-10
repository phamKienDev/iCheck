package vn.icheck.android.base.dialog.notify.tick_used_topup

import android.content.Context
import android.os.Handler
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog

abstract class DialogOnTickUsedTopup(context: Context, private val isCancelable: Boolean) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_tick_used_topup

    override val getIsCancelable: Boolean
        get() = isCancelable

    override fun onInitView() {
        Handler().postDelayed({
            dismiss()
        },1000)
    }
}