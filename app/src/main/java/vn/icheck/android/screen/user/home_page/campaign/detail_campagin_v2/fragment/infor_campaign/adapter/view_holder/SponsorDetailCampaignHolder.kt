package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.adapter.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.sponsor_detail_campaign.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.adapter.SponsorsAdapter

class SponsorDetailCampaignHolder (parent: ViewGroup) : BaseViewHolder<ICDetail_Campaign>(LayoutInflater.from(parent.context).inflate(R.layout.sponsor_detail_campaign, parent, false)) {

    private var adapter : SponsorsAdapter? = null

    override fun bind(obj: ICDetail_Campaign) {
        adapter = SponsorsAdapter()
        val layoutManager = GridLayoutManager(itemView.context, 3, GridLayoutManager.VERTICAL, false)
        itemView.rcvSponsSors.layoutManager = layoutManager
        itemView.rcvSponsSors.adapter = adapter

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(p0: Int): Int {
                return if (adapter?.listSponsor!!.isEmpty())
                    3
                else
                    1
            }
        }
    }
}