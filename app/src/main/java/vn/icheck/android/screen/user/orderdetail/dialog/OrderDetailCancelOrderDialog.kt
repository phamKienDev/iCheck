package vn.icheck.android.screen.user.orderdetail.dialog

import android.content.Context
import android.widget.RadioButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_order_detail_cancel_order.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICOrderDetail
import vn.icheck.android.util.kotlin.ToastUtils

abstract class OrderDetailCancelOrderDialog(val context: Context, val obj: ICOrderDetail) {

    fun show() {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        dialog.setContentView(R.layout.dialog_order_detail_cancel_order)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.tvDone.setOnClickListener {
            val buttonID = dialog.radioGroup.checkedRadioButtonId
            val button = dialog.radioGroup.findViewById<RadioButton?>(buttonID)

            if (button != null) {
                dialog.dismiss()
                onDone(obj.id, button.text.toString())
            } else {
                ToastUtils.showShortError(context, R.string.vui_long_chon_ly_do)
            }
        }

        dialog.show()
    }

    protected abstract fun onDone(orderID: Long, reason: String)
}