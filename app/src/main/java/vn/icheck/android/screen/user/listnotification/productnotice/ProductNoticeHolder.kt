package vn.icheck.android.screen.user.listnotification.productnotice

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_product_notice.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.notification.NotificationInteractor
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.network.models.ICReqMarkRead
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductNoticeHolder(parent: ViewGroup) : BaseViewHolder<ICNotification>(LayoutInflater.from(parent.context).inflate(R.layout.item_product_notice, parent, false)) {
    private var listener: View.OnClickListener? = null

    override fun bind(obj: ICNotification) {
        checkRead(obj.status)

        WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.sourceUser?.firstOrNull()?.avatar, R.drawable.ic_business_v2)

        val name = obj.sourceUser?.firstOrNull()?.getName
        itemView.tvTitle.text = Html.fromHtml(itemView.context.getString(R.string.html_bold_xxx_xxx, name, obj.description))
        itemView.tvDate.text = TimeHelper.convertDateTimeSvToTimeDateVnPhay(obj.createdAt)

        itemView.setOnClickListener {
            readNotification(obj, false)
            ICheckApplication.currentActivity()?.let { activity ->
                FirebaseDynamicLinksActivity.startTarget(activity, obj.targetType, obj.targetId)
            }
        }

        itemView.imgOption.setOnClickListener {
            showOption(obj)
        }
    }

    private fun checkRead(status: Int) {
        if (status == 1) {
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
            object : ProductNoticeOptionDialog(activity) {
                override fun onRead() {
                    readNotification(obj, true)
                }

                override fun onRemove() {
                    removeNotification(obj)
                }
            }.show(obj)
        }
    }

    private fun readNotification(objNotification: ICNotification, isShowLoading: Boolean) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                if (isShowLoading) {
                    ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
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

                    objNotification.status = 1
                    checkRead(objNotification.status)
                }

                override fun onError(error: ICResponseCode?) {
                    if (isShowLoading) {
                        DialogHelper.closeLoading(activity)
                        ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
            })
        }
    }

    private fun removeNotification(obj: ICNotification) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            NotificationInteractor().deleteNotification(obj.id, object : ICNewApiListener<ICResponse<Any>> {
                override fun onSuccess(obj: ICResponse<Any>) {
                    DialogHelper.closeLoading(activity)

                    listener?.onClick(null)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }
}