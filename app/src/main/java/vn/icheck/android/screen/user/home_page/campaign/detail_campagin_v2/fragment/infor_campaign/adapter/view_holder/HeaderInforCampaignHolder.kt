package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.adapter.view_holder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.header_infor_campaign.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.util.kotlin.WidgetUtils

class HeaderInforCampaignHolder(parent: ViewGroup) : BaseViewHolder<ICDetail_Campaign>(LayoutInflater.from(parent.context).inflate(R.layout.header_infor_campaign, parent, false)) {
    @SuppressLint("SetTextI18n")
    override fun bind(obj: ICDetail_Campaign) {
        WidgetUtils.loadImageUrl(itemView.imgBannerCampaign,obj.image)
        itemView.tvNameCampaign.text = obj.title

        if (obj.beginAt.isNullOrEmpty() || obj.endedAt.isNullOrEmpty()) {
            itemView.tvTimeEnd.text = itemView.context.getString(R.string.dang_cap_nhat)
        } else {
            itemView.tvTimeEnd.text = "Đến ${TimeHelper.convertDateTimeSvToDayMonthVn(obj.endedAt)}"
        }

        itemView.tvUserJoin.text = obj.successNumber.toString()
    }
}