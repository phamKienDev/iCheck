package vn.icheck.android.screen.dialog

import android.content.Context
import androidx.appcompat.widget.AppCompatImageButton
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog

abstract class NotShippingShopProductDialog(context: Context) : BaseBottomSheetDialog(context, R.layout.dialog_not_shipping_shop_product, true) {

    fun show() {
        val imgClose = dialog.findViewById<AppCompatImageButton>(R.id.img_close)

        imgClose?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    protected abstract fun onDismiss()
}