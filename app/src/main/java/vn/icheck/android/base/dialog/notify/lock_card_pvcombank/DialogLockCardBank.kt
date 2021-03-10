package vn.icheck.android.base.dialog.notify.lock_card_pvcombank

import android.content.Context
import kotlinx.android.synthetic.main.dialog_lock_card_pvcombank.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

abstract class DialogLockCardBank( context: Context, val title: Int, val message: Int, val isCancelable: Boolean) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_lock_card_pvcombank

    override val getIsCancelable: Boolean
        get() = isCancelable

    override fun onInitView() {
        txtTitle.setText(title)
        txtMessage.setText(message)

        btnDisagree.setOnClickListener {
            dismiss()
            onDisagree()
        }

        btnAgree.setOnClickListener {
            dismiss()
            onAgree()
        }
    }

    protected abstract fun onDisagree()
    protected abstract fun onAgree()
}