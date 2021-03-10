package vn.icheck.android.loyalty.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_scan_loyalty_error.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

abstract class DialogErrorScanGame(context: Context, val icon: Int, val title: String, val message: String) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_scan_loyalty_error
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        imgIcon.setImageResource(icon)
        tvTitle.text = title
        tvMessage.text = message

        imgClose.setOnClickListener {
            dismiss()
        }

        btnScan.setOnClickListener {
            dismiss()
        }

        setOnDismissListener {
            onDismiss()
        }
    }

    abstract fun onDismiss()
}