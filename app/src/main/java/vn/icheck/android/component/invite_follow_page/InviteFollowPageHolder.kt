package vn.icheck.android.component.invite_follow_page

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_invite_follow_page_holder.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICPageOverview
import vn.icheck.android.screen.user.invite_friend_follow_page.InviteFriendFollowPageActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.visibleOrInvisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class InviteFollowPageHolder(parent: ViewGroup) : BaseViewHolder<ICPageOverview>(LayoutInflater.from(parent.context).inflate(R.layout.item_invite_follow_page_holder, parent, false)) {
    override fun bind(obj: ICPageOverview) {
        if (obj.isFollow && !obj.isIgnoreInvite) {
            itemView.layoutParams = ViewHelper.createLayoutParams().also {
                it.setMargins(0, SizeHelper.dpToPx(10), 0, 0)
            }
            for (i in 0 until (itemView as ViewGroup).childCount)
                itemView.getChildAt(i).beVisible()


            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)
            itemView.tvName.apply {
                text = context.getString(R.string.ban_da_theo_doi_s, obj.name)
            }

            itemView.tvInvite.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    ActivityUtils.startActivity<InviteFriendFollowPageActivity, Long>(it, Constant.DATA_1, obj.id!!)
                }
            }

            itemView.tvSkip.setOnClickListener {
                obj.id?.let { id -> skipInvite(id) }
            }
        } else {
            itemView.layoutParams = ViewHelper.createLayoutParams().also {
                it.setMargins(0, 0, 0, 0)
            }
            for (i in 0 until (itemView as ViewGroup).childCount)
                itemView.getChildAt(i).beGone()
        }
    }

    private fun skipInvite(pageId: Long) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }
            DialogHelper.showLoading(activity)
            PageRepository().skipInvite(pageId, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SKIP_INVITE_FOLLOW_PAGE))
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }
}