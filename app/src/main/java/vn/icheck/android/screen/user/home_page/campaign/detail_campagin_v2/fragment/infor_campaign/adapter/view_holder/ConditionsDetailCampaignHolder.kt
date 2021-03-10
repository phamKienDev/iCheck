package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.adapter.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.condition_detail_campaign.view.*
import kotlinx.android.synthetic.main.item_condition_campaign.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.text_view.TextViewBarlowMedium
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.adapter.ConditionsAdapter

class ConditionsDetailCampaignHolder (parent: ViewGroup) : BaseViewHolder<ICDetail_Campaign>(LayoutInflater.from(parent.context).inflate(R.layout.condition_detail_campaign, parent, false)) {

    private var adapterCondition: ConditionsAdapter? = null

    override fun bind(obj: ICDetail_Campaign) {
        adapterCondition = ConditionsAdapter(itemView.context)
        itemView.rcvConditions.layoutManager = LinearLayoutManager(itemView.context)
        itemView.rcvConditions.adapter = adapterCondition
    }
}