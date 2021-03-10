package vn.icheck.android.screen.user.checkoutcart.dialog

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_select_shipping.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICShipping
import vn.icheck.android.screen.user.checkoutcart.adapter.SelectShippingAdapter
import vn.icheck.android.util.kotlin.ToastUtils

abstract class SelectShippingDialog(val context: Context, shippingID: Int, listData: MutableList<ICShipping>) {
    val adapter = SelectShippingAdapter(shippingID, listData)

    fun show() {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        dialog.setContentView(R.layout.dialog_select_shipping)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.recyclerView.adapter = adapter

        dialog.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btnDone.setOnClickListener {
            val obj = adapter.getSelectedShipping

            if (obj == null) {
                ToastUtils.showShortWarning(context, R.string.vui_long_chon_don_vi_van_chuyen)
            } else {
                dialog.dismiss()
                onSelected(obj)
            }
        }

        dialog.show()
    }

    protected abstract fun onSelected(obj: ICShipping)
}