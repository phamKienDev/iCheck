package vn.icheck.android.screen.dialog

import android.content.Context
import android.text.Html
import kotlinx.android.synthetic.main.dialog_call_phone.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog

abstract class CallPhoneDialog(context: Context, val phone: String) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_call_phone
    override val getIsCancelable: Boolean
        get() = true

    override fun onInitView() {
        tvContent.text = Html.fromHtml("Gọi <font color=${vn.icheck.android.ichecklibs.Constant.getPrimaryColorCode}>*100*${phone}#</font>")

        tvCancel.setOnClickListener {
            dismiss()
            actionCancel()
        }
        tvOk.setOnClickListener {
            dismiss()
            actionOk(phone)
        }

    }

    abstract fun actionCancel()
    abstract fun actionOk(phone: String)
}