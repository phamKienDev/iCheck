package vn.icheck.android.screen.user.listnotification.othernotification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_other_notification.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.notification.NotificationInteractor
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.network.models.ICReqMarkRead
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.rank_of_user.RankOfUserActivity
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ToastUtils

class OtherNotificationHolder(parent: ViewGroup) : BaseViewHolder<ICNotification>(
    LayoutInflater.from(parent.context).inflate(R.layout.item_other_notification, parent, false)
) {
    private var listener: View.OnClickListener? = null

    override fun bind(obj: ICNotification) {
        itemView.imgAvatar.background=vn.icheck.android.ichecklibs.ViewHelper.bgTransparentStrokeLineColor1Corners4(itemView.context)
        checkRead(obj.isReaded == true)
        if (obj.showTitle) {
            itemView.title.beVisible()
        } else {
            itemView.title.beGone()
        }
//        WidgetUtils.loadImageUrlRounded(itemView.imgAvatar, obj.sourceUser?.firstOrNull()?.avatar, R.drawable.ic_error_ic_image, SizeHelper.size4)
        itemView.imgAvatar.setImageResource(R.drawable.ic_icheck_logo)
        when (obj.targetEntity) {
            "system_score_changed" -> {
                itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_notification_icoin_20dp,
                    0
                )
            }
            "system_gift_changed" -> {
                itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_notification_gift_20dp,
                    0
                )
            }
            "RANK", "LEVEL_UP", "POST" -> {
                itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_notification_admin_20dp,
                    0
                )
            }
            "system_voucher_changed" -> {
                itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_notification_voucher_20dp,
                    0
                )
            }
            "USER" -> {
                if (obj.action == "MISSION_FINISH") {
                    itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_notification_gift_20dp,
                        0
                    )
                } else if (obj.action == "SYSTEM") {
                    itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_notification_admin_20dp,
                        0
                    )
                }
            }
            else -> {
                itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_notification_admin_20dp,
                    0
                )
            }
        }

        itemView.tvTitle.text = obj.title ?: obj.description
        if (!obj.title.isNullOrEmpty()) {
            itemView.tvMessage.text = obj.description
            itemView.tvMessage.beVisible()
        } else {
            itemView.tvMessage.beGone()
        }
        itemView.tvDate.text = obj.createdAt?.getNotifyTime()

        itemView.setOnClickListener {
            readNotification(obj, false)
        }

        itemView.imgOption.onDelayClick({
            showOption(obj)
        }, 1500)
    }

    private fun checkRead(isRead: Boolean) {
        if (isRead) {
            itemView.root.setBackgroundResource(ViewHelper.outValue.resourceId)
        } else {
            itemView.root.background = ViewHelper.createStateListDrawable(
                ContextCompat.getColor(itemView.context, R.color.unread_notification_color),
                ContextCompat.getColor(itemView.context, R.color.unread_notification_color),
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                0f
            )
        }
    }

    fun setOnRemoveListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    private fun showOption(obj: ICNotification) {
        ICheckApplication.currentActivity()?.let { activity ->
            object : OtherNotificationOptionDialog(activity) {
                override fun onRead() {
                    readNotification(obj, isShowLoading = true, targetAction = false)
                }

                override fun onRemove() {
                    removeNotification(obj)
                }
            }.show(obj)
        }
    }

    private fun readNotification(
        objNotification: ICNotification,
        isShowLoading: Boolean,
        targetAction: Boolean = true
    ) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                if (isShowLoading) {
                    ToastUtils.showLongError(
                        activity,
                        R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai
                    )
                }

                return
            }

            if (isShowLoading) {
                DialogHelper.showLoading(activity)
            }

            NotificationInteractor().markReadNotification(
                ICReqMarkRead(
                    idList = arrayListOf(
                        objNotification.id
                    ), isAll = false
                ), object : ICNewApiListener<ICResponse<Boolean>> {
                    override fun onSuccess(obj: ICResponse<Boolean>) {
                        if (isShowLoading) {
                            DialogHelper.closeLoading(activity)
                        }
                        if (objNotification.isReaded == false && isShowLoading) {
                            itemView.context.showSimpleSuccessToast("Bạn đã đọc thông báo này")
                        }
                        objNotification.isReaded = true
                        checkRead(objNotification.isReaded == true)
                        if (targetAction) {
                            targetAction(objNotification)
                        }
                    }

                    override fun onError(error: ICResponseCode?) {
                        if (isShowLoading) {
                            DialogHelper.closeLoading(activity)
                            ToastUtils.showLongError(
                                activity,
                                R.string.co_loi_xay_ra_vui_long_thu_lai
                            )
                        }
                        if (targetAction) {
                            targetAction(objNotification)
                        }
                    }
                })
        }
    }

    private fun targetAction(obj: ICNotification) {
        when (obj.targetEntity) {
            "system_score_changed" -> {
                itemView.context.simpleStartActivity(RankOfUserActivity::class.java)
            }
            "system_gift_changed" -> {
                FirebaseDynamicLinksActivity.startTargetPath(itemView.context, obj.path)
            }
            "RANK", "LEVEL_UP" -> {
                itemView.context.simpleStartActivity(RankOfUserActivity::class.java)
            }
            "system_voucher_changed" -> {
                FirebaseDynamicLinksActivity.startTargetPath(itemView.context, obj.path)
            }
            "USER" -> {
                if (obj.action == "MISSION_FINISH") {
                    FirebaseDynamicLinksActivity.startTargetPath(itemView.context, obj.path)
                    return
                }

                ICheckApplication.currentActivity()?.let { activity ->
                    FirebaseDynamicLinksActivity.startDestinationUrl(
                        activity,
                        obj.path ?: obj.redirectPath
                    )
                }
            }
            else -> {
                FirebaseDynamicLinksActivity.startTargetPath(itemView.context, obj.path)
            }
        }
    }

    private fun removeNotification(obj: ICNotification) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(
                    activity,
                    R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai
                )
                return
            }

            DialogHelper.showLoading(activity)

            NotificationInteractor().deleteNotification(
                obj.id,
                object : ICNewApiListener<ICResponse<Any>> {
                    override fun onSuccess(obj: ICResponse<Any>) {
                        DialogHelper.closeLoading(activity)

                        listener?.onClick(null)
                        itemView.context.showSimpleSuccessToast("Xóa thông báo thành công")
                    }

                    override fun onError(error: ICResponseCode?) {
                        DialogHelper.closeLoading(activity)
                        ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                })
        }
    }
}