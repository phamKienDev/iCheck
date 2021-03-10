package vn.icheck.android.screen.user.orderdetail.dialog

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_order_detail_cancel_detail.*
import vn.icheck.android.R
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICOrderDetail
import vn.icheck.android.screen.user.orderhistory.OrderHistoryActivity

abstract class OrderDetailCancelDetailDialog(val context: Context, val obj: ICOrderDetail) {

    fun show() {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        dialog.setContentView(R.layout.dialog_order_detail_cancel_detail)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

//        dialog.tvOrder.visibility = if (obj.reward == "reward" || obj.reward == "exchange") {
//            View.GONE
//        } else {
//            View.VISIBLE
//        }

        dialog.tvName.text = if (obj.cancelled_by_user_id != null) {
            context.getString(R.string.nguoi_mua)
        } else {
            context.getString(R.string.nguoi_ban)
        }

        dialog.tvDate.text = TimeHelper.convertDateTimeSvToTimeDateVnPhay(getCancelAt)
        dialog.tvNote.text = obj.cancel_reason

        dialog.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.tvOrder.setOnClickListener {
            dialog.dismiss()
            onOrderAgaign(obj)
        }

        dialog.show()
    }

    private val getCancelAt: String?
        get() {
            for (it in obj.status_history) {
                if (it.status == OrderHistoryActivity.canceled) {
                    return it.createdAt
                }
            }

            return null
        }

    protected abstract fun onOrderAgaign(obj: ICOrderDetail)
}