package vn.icheck.android.screen.user.listnotification.viewholder.pagerelated

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICFriendSuggestion
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.network.models.ICNotificationPage
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.ui.view.SquareImageView
import vn.icheck.android.util.ick.showSimpleSuccessToast
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class NotificationPageAdapter : RecyclerView.Adapter<NotificationPageAdapter.PageRelatedHolder>() {
    private val listData = mutableListOf<ICNotificationPage>()

    fun setData(list: MutableList<ICNotificationPage>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = listData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageRelatedHolder =
            PageRelatedHolder(parent)

    override fun onBindViewHolder(holder: PageRelatedHolder, position: Int) {
        holder.bind(listData[position])
    }

    class PageRelatedHolder(parent: ViewGroup) : BaseViewHolder<ICNotificationPage>(LayoutInflater.from(parent.context).inflate(R.layout.item_notification_page, parent, false)) {

        override fun bind(obj: ICNotificationPage) {
            (itemView as ViewGroup).apply {
                WidgetUtils.loadImageUrlRounded(getChildAt(0) as SquareImageView, obj.avatar, R.drawable.ic_business_v2, SizeHelper.size4)
                getChildAt(0).setOnClickListener {
                    PageDetailActivity.start(itemView.context, obj.id)
                }
                (getChildAt(1) as AppCompatTextView).text = obj.name

                getChildAt(2).setOnClickListener {
                    followPage(obj)
                }

                checkFollow(obj.userFollowIdList?.contains(SessionManager.session.user?.id))
            }
        }

        private fun checkFollow(isFollow: Boolean?) {
            if (isFollow == true) {
                ((itemView as ViewGroup).getChildAt(2) as AppCompatTextView).apply {
                    isEnabled = false
                    setText(R.string.dang_theo_doi)
                }
            } else {
                ((itemView as ViewGroup).getChildAt(2) as AppCompatTextView).apply {
                    isEnabled = true
                    setText(R.string.theo_doi)
                }
            }
        }

        private fun followPage(objSuggestion: ICNotificationPage) {
            ICheckApplication.currentActivity()?.let { activity ->
                if (NetworkHelper.isNotConnected(activity)) {
                    ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    return
                }

                DialogHelper.showLoading(activity)

                PageRepository().followPage(objSuggestion.id, object : ICNewApiListener<ICResponse<Boolean>> {
                    override fun onSuccess(obj: ICResponse<Boolean>) {
                        DialogHelper.closeLoading(activity)
                        objSuggestion.isFollow = true
                        checkFollow(objSuggestion.isFollow)
                    }

                    override fun onError(error: ICResponseCode?) {
                        DialogHelper.closeLoading(activity)
                        ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                })
            }
        }
    }
}