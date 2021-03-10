package vn.icheck.android.base.dialog.notify.comfirm_da_lay_qua

import android.content.Context
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.component.popup_view.BasePopupView

abstract class DialogComfirmDaLayQua (context: Context, private val isCancelable: Boolean) : BasePopupView(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_da_lay_qua

    override val getIsCancelable: Boolean
        get() = isCancelable

    override fun onInitView() {

        findViewById<AppCompatTextView>(R.id.btnComfirmDaLay)?.run {
            setOnClickListener {
                dismiss()
                onGivePoint()
            }
        }

        findViewById<LinearLayout>(R.id.layoutBackground)?.run {
            setOnClickListener {
                dismiss()
                onClose()
            }
        }
    }

    protected abstract fun onClose()
    protected abstract fun onGivePoint()
}