package vn.icheck.android.screen.user.listnotification.interactive

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_interactive.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.notification.NotificationInteractor
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.network.models.ICReqMarkRead
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class InteractiveHolder(parent: ViewGroup) : BaseViewHolder<ICNotification>(LayoutInflater.from(parent.context).inflate(R.layout.item_interactive, parent, false)) {
    private var listener: View.OnClickListener? = null

    override fun bind(obj: ICNotification) {
        checkRead(obj.isReaded)
        if (obj.sourceUser?.firstOrNull()?.entity == "PAGE") {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.sourceUser?.firstOrNull()?.avatar, R.drawable.ic_business_v2)
        } else {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.sourceUser?.firstOrNull()?.avatar, R.drawable.ic_user_svg)
        }

        itemView.tvTitle simpleText obj.message
        itemView.tvRank.apply {
            if (obj.sourceUser?.firstOrNull()?.entity == "USER") {
                beVisible()
                when (obj.sourceUser?.firstOrNull()?.rank?.level) {
                    Constant.USER_LEVEL_SILVER -> {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_rank_silver_16dp, 0)
                    }
                    Constant.USER_LEVEL_GOLD -> {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_rank_gold_16dp, 0)
                    }
                    Constant.USER_LEVEL_DIAMOND -> {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_rank_diamond_16dp, 0)
                    }
                    else -> {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_rank_standard_16dp, 0)
                    }
                }
            } else {
                beGone()
            }
        }

        when (obj.action) {
            "INVITE_FOLLOW","ADD_PRODUCT","INVITE_FRIEND", "CONTRIBUTE" -> {
                itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_follow_20dp, 0)
            }
            "LIKE_REVIEW", "LIKE", "LIKE_POST" -> {
                itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_like_20dp, 0)
            }
            "COMMENT_REVIEW","COMMENT_REVIEW_MANY", "COMMENT" -> {
                itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_comment_20dp, 0)
            }
            else -> {
                itemView.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_follow_20dp, 0)
            }
        }

//        itemView.tvTime.text = TimeHelper.convertDateTimeSvToCurrentDate(obj.createdAt)
        itemView.tvTime simpleText obj.createdAt?.getNotifyTime()
        itemView.imgOption.onDelayClick({
            showOption(obj)
        },1500)

        itemView.setOnClickListener {
            readNotification(obj, false)
            ICheckApplication.currentActivity()?.let { activity ->
                FirebaseDynamicLinksActivity.startTargetPath(activity, obj.path)
            }
        }
    }

    private fun checkRead(isReaded: Boolean?) {
        if (isReaded == true) {
            itemView.setBackgroundResource(ViewHelper.outValue.resourceId)
        } else {
            itemView.background = ViewHelper.createStateListDrawable(
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
            object : InteractiveOptionDialog(activity) {
                override fun onRead() {
                    readNotification(obj, true)
                }

                override fun onTurnOffNotification() {
                    if (obj.isTurnOff == false) {
                        unsubscribeNotification(obj)
                    } else {
                        subscribeNotification(obj)
                    }
                }

                override fun onRemove() {
                    removeNotification(obj)
                }
            }.show(obj, type = "tương tác")
        }
    }

    private fun readNotification(objNotification: ICNotification, isShowLoading: Boolean) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                if (isShowLoading) {
                    itemView.context.showSimpleErrorToast(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
//                    ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                }

                return
            }

            if (isShowLoading) {
                DialogHelper.showLoading(activity)
            }

            NotificationInteractor().markReadNotification(ICReqMarkRead(objNotification.id), object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    if (isShowLoading) {
                        DialogHelper.closeLoading(activity)
                    }
                    if (objNotification.isReaded == false && isShowLoading) {
                        itemView.context.showSimpleSuccessToast("Bạn đã đọc thông báo này")
                    }
                    objNotification.isReaded = true
                    objNotification.status = 1
                    checkRead(objNotification.isReaded == true)
                }

                override fun onError(error: ICResponseCode?) {
                    if (isShowLoading) {
                        DialogHelper.closeLoading(activity)
                        itemView.context.showSimpleErrorToast(itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
//                        ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
            })
        }
    }

    private fun unsubscribeNotification(objNotification: ICNotification) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                itemView.context.showSimpleErrorToast(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
//                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            NotificationInteractor().unsubscribeNotification(objNotification.id, "post", object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                    objNotification.isTurnOff = true
                    itemView.context.showSimpleSuccessToast("Bạn đã tắt thông báo cho đối tượng này")
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    itemView.context.showSimpleErrorToast(itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
//                    ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }

    private fun subscribeNotification(objNotification: ICNotification) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
//                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                itemView.context.showSimpleErrorToast(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            DialogHelper.showLoading(activity)

            PageRepository().subcribeNotification(objNotification.id, "post", object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                    objNotification.isTurnOff = false
                    itemView.context.showSimpleSuccessToast("Bạn đã bật thông báo cho đối tượng này")
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    itemView.context.showSimpleErrorToast(itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
//                    ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }

    private fun removeNotification(obj: ICNotification) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                itemView.context.showSimpleErrorToast(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
//                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            NotificationInteractor().deleteNotification(obj.id, object : ICNewApiListener<ICResponse<Any>> {
                override fun onSuccess(obj: ICResponse<Any>) {
                    DialogHelper.closeLoading(activity)

                    listener?.onClick(null)
                    itemView.context.showSimpleSuccessToast("Xóa thông báo thành công")
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
//                    ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                    itemView.context.showSimpleErrorToast(itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }
}