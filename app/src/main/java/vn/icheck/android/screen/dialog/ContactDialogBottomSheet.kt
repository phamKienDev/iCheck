package vn.icheck.android.screen.dialog

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.dialog_shop_sort.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.util.ick.rText

abstract class ContactDialogBottomSheet(context: Context, private var phone: String) : BaseBottomSheetDialog(context, R.layout.dialog_shop_sort, true) {

    fun show() {
        dialog.tvSort.visibility = View.GONE
        dialog.tvDefault.visibility = View.GONE
        dialog.tvLowtoHigh.visibility = View.GONE
        dialog.tvHightoLow.visibility = View.GONE
        dialog.tvNew.text = phone
        dialog.tvTheBestSeller rText R.string.gui_tin_nhan
        dialog.tvClose rText R.string.huy

        dialog.view.visibility = View.GONE
        dialog.view1.visibility = View.GONE
        dialog.view2.visibility = View.GONE
        dialog.view3.visibility = View.GONE

        dialog.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.tvNew.setOnClickListener {
            dialog.dismiss()
            onClickPhone()
        }

        dialog.tvTheBestSeller.setOnClickListener {
            dialog.dismiss()
            onClickMessage()
        }

        dialog.show()
    }

    protected abstract fun onClickPhone()
    protected abstract fun onClickMessage()
}