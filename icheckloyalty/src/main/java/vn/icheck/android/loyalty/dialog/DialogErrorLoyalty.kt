package vn.icheck.android.loyalty.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_show_error_loyalty.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

open class DialogErrorLoyalty(context: Context, val icon: Int, val message: String) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_show_error_loyalty
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        imgError.setImageResource(icon)
        tvMessage.text = message

        imgClose.setOnClickListener {
            dismiss()
        }
    }
}