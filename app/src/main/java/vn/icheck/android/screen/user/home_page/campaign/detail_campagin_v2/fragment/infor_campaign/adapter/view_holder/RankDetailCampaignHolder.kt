package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.adapter.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.rank_detail_campaign.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.adapter.UserRankAdapter

class RankDetailCampaignHolder(parent: ViewGroup) : BaseViewHolder<ICDetail_Campaign>(LayoutInflater.from(parent.context).inflate(R.layout.rank_detail_campaign, parent, false)) {

    private var adapterRank: UserRankAdapter? = null

    override fun bind(obj: ICDetail_Campaign) {
        adapterRank = UserRankAdapter(itemView.context)
        val layoutManager = GridLayoutManager(itemView.context,2, GridLayoutManager.VERTICAL,false)
        itemView.rcvRank.layoutManager = layoutManager
        itemView.rcvRank.adapter = adapterRank

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(p0: Int): Int {
                return if (adapterRank?.listRank!!.isEmpty())
                    2
                else
                    1
            }
        }
    }
}