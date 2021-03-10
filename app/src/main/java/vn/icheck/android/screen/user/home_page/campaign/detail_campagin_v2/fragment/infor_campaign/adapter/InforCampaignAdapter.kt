package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.adapter.view_holder.*
import vn.icheck.android.screen.user.home_page.home.model.ICHomeItem

class InforCampaignAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICHomeItem>()

    var sharedPool: RecyclerView.RecycledViewPool? = null
//    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
//        super.onAttachedToRecyclerView(recyclerView)
//        sharedPool = recyclerView.recycledViewPool
//    }

    @Synchronized
    fun addData(list: MutableList<ICHomeItem>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData[position].data != null) {
            listData[position].viewType
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.HEADER_INFOR_CAMPAIGN -> {
                HeaderInforCampaignHolder(parent)
            }
            ICViewTypes.INFOR_CAMPAIGN -> {
                InformationDetailCampaignHolder(parent)
            }
            ICViewTypes.CONDITIONS_CAMPAIGN -> {
                ConditionsDetailCampaignHolder(parent)
            }
            ICViewTypes.RANK_CAMPAIGN -> {
                RankDetailCampaignHolder(parent)
            }
            ICViewTypes.GUIDE_CAMPAIGN -> {
                GuideDetailCampaignHolder(parent)
            }
            ICViewTypes.SPONSOR_CAMPAIGN -> {
                SponsorDetailCampaignHolder(parent)
            }
            else -> NullHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderInforCampaignHolder -> {
                holder.bind(listData[position].data as ICDetail_Campaign)
            }
            is InformationDetailCampaignHolder -> {
                holder.bind(listData[position].data as ICDetail_Campaign)
            }
            is ConditionsDetailCampaignHolder -> {
                holder.bind(listData[position].data as ICDetail_Campaign)
            }
            is RankDetailCampaignHolder -> {
                holder.bind(listData[position].data as ICDetail_Campaign)
            }
            is GuideDetailCampaignHolder -> {
                holder.bind(listData[position].data as ICDetail_Campaign)
            }
            is SponsorDetailCampaignHolder -> {
                holder.bind(listData[position].data as ICDetail_Campaign)
            }
        }
    }
}