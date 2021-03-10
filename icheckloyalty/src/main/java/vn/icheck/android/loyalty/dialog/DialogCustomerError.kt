package vn.icheck.android.loyalty.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_customer_error.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

abstract class DialogCustomerError(context: Context,
                                   private val icon: Int,
                                   private val title: String,
                                   private val message: String) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_customer_error
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        imgIcon.setImageResource(icon)
        tvTitle.text = title

        tvMessage.text = message

        imgClose.setOnClickListener {
            dismiss()
        }

        setOnDismissListener {
            onDismiss()
        }
    }

    protected abstract fun onDismiss()
}