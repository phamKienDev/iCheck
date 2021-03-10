package vn.icheck.android.screen.user.orderdetail.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TableRow
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_order_detail_status_history.*
import vn.icheck.android.R
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICStatusHistory
import vn.icheck.android.screen.user.orderhistory.OrderHistoryActivity

abstract class OrderDetailStatusHistoryDialog(val context: Context, val type: Int, val list: MutableList<ICStatusHistory>) {

    fun show() {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        dialog.setContentView(R.layout.dialog_order_detail_status_history)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        val inflater = LayoutInflater.from(context)

        for (it in list) {
            val children = inflater.inflate(R.layout.dialog_order_detail_status_history_item, dialog.tableLayout, false) as TableRow

            val title = children.getChildAt(0) as AppCompatTextView
            val value = children.getChildAt(1) as AppCompatTextView

            title.text = it.comment
            value.text = TimeHelper.convertDateTimeSvToTimeDateVnPhay(it.createdAt)

            dialog.tableLayout.addView(children)
        }

        if (type == OrderHistoryActivity.delivery) {
            dialog.tvDone.visibility = View.VISIBLE
        }

        dialog.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.tvDone.setOnClickListener {
            dialog.dismiss()
            onDoneOrder()
        }

        dialog.show()
    }

    protected abstract fun onDoneOrder()
}