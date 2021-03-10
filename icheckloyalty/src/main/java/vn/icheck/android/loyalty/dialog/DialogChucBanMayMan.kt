package vn.icheck.android.loyalty.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_chuc_ban_may_man.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

open class DialogChucBanMayMan(context: Context) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_chuc_ban_may_man
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        btnClose.setOnClickListener {
            dismiss()
        }
    }
}