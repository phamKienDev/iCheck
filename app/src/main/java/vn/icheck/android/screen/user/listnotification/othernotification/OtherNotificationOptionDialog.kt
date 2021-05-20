package vn.icheck.android.screen.user.listnotification.othernotification

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.dialog_other_notification_option.*
import kotlinx.android.synthetic.main.item_other_notification.view.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.util.kotlin.WidgetUtils

abstract class OtherNotificationOptionDialog(context: Context) : BaseBottomSheetDialog(context, R.layout.dialog_other_notification_option, true) {

    fun show(obj: ICNotification) {
//        WidgetUtils.loadImageUrl(dialog.imgAvatar, obj.sourceUser?.firstOrNull()?.avatar, R.drawable.ic_circle_avatar_default)
        dialog.imgAvatar.background=ViewHelper.bgTransparentRadius4StrokeLineColor1(dialog.context)
        dialog.imgAvatar.setImageResource(R.drawable.ic_icheck_logo)
        when (obj.targetEntity) {
            "system_score_changed" -> {
                dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_icoin_20dp, 0)
            }
            "system_gift_changed" -> {
                dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_gift_20dp, 0)
            }
            "RANK","LEVEL_UP","POST" -> {
                dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_admin_20dp, 0)
            }
            "system_voucher_changed" -> {
                dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_voucher_20dp, 0)
            }
            "USER" -> {
                if (obj.action == "MISSION_FINISH") {
                    dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_gift_20dp, 0)
                }
            }
            else -> {
                dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        dialog.tvTitle.text = obj.message ?: obj.description

        dialog.layoutContent.apply {
            getChildAt(0).apply {
                if (obj.status == 1 || obj.isReaded == true) {
                    visibility = View.GONE
                }

                setOnClickListener {
                    dialog.dismiss()
                    onRead()
                }
            }

            getChildAt(1).setOnClickListener {
                dialog.dismiss()
                onRemove()
            }
        }

        dialog.show()
    }

    protected abstract fun onRead()
    protected abstract fun onRemove()
}