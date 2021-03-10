package vn.icheck.android.base.dialog.notify.accecp_ship_gift

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog

abstract class DialogAcceptShipGift (context: Context, private val isCancelable: Boolean): BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_accept_ship_gift

    override val getIsCancelable: Boolean
        get() = isCancelable

    override fun onInitView() {
        findViewById<AppCompatImageView>(R.id.btnClose)?.run {
            setOnClickListener {
                dismiss()
                onClose()
            }
        }

        findViewById<AppCompatTextView>(R.id.btnDetailShipGift)?.run {
            setOnClickListener {
                dismiss()
                onGivePoint()
            }
        }
    }

    protected abstract fun onClose()
    protected abstract fun onGivePoint()
}