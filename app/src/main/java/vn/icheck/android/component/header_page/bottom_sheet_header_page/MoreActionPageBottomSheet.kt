package vn.icheck.android.component.header_page.bottom_sheet_header_page

import android.content.Context
import kotlinx.android.synthetic.main.dialog_more_action_page.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICPageOverview

abstract class MoreActionPageBottomSheet(context: Context, val obj: ICPageOverview) : BaseBottomSheetDialog(context, R.layout.dialog_more_action_page, true) {

    fun show() {
        dialog.linearLayout.background=ViewHelper.bgWhiteCornersTop20(dialog.context)
        if (obj.isFollow) {
            dialog.tvFollow.text = "Bỏ theo dõi Trang này"
            dialog.tvSubUnfollow.text = "Khi bỏ theo dõi, các bài viết của ${obj.name} này sẽ không hiển thị với bạn nữa."
        } else {
            dialog.tvFollow.text = "Theo dõi Trang này"
            dialog.tvSubUnfollow.text = "Khi theo dõi, các bài viết của ${obj.name} sẽ hiển thị đầy đủ với bạn. "
        }

        if (!obj.unsubscribeNotice) {
            dialog.tv_notify.text = "Bật thông báo cho Trang này"
            dialog.tvNotificationOn.text = "Bạn sẽ nhận được thông báo khi ${obj.name} đăng nội dung mới."
            dialog.imgNotification.setImageResource(R.drawable.ic_turn_on_notification_40px)
        } else {
            dialog.tv_notify.text = "Tắt thông báo cho Trang này"
            dialog.tvNotificationOn.text = "Bạn sẽ không nhận được thông báo khi ${obj.name} đăng nội dung mới."
            dialog.imgNotification.setImageResource(R.drawable.ic_turn_off_notification_40dp)
        }

        dialog.layoutUnfollow.setOnClickListener {
            dialog.dismiss()
            onClickUnfollow()
        }

        dialog.layoutStateNotification.setOnClickListener {
            dialog.dismiss()
            onClickStateNotification()
        }

        dialog.layoutReport.setOnClickListener {
            dialog.dismiss()
            onClickReportPage()
        }

        dialog.show()
    }

    protected abstract fun onClickUnfollow()
    protected abstract fun onClickStateNotification()
    protected abstract fun onClickReportPage()
}