package vn.icheck.android.screen.user.detail_my_reward

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.screen.user.detail_my_reward.holder.HeaderInforRewardHolder
import vn.icheck.android.screen.user.detail_my_reward.holder.InformationDetailRewardHolder
import vn.icheck.android.screen.user.detail_my_reward.holder.SupplierRewardHolder
import vn.icheck.android.screen.user.home_page.model.ICHomeItem

class DetailMyRewardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICHomeItem>()

    override fun getItemCount(): Int {
        return listData.size
    }

    @Synchronized
    fun addData(list: MutableList<ICHomeItem>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
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
                HeaderInforRewardHolder(parent)
            }
            ICViewTypes.INFOR_CAMPAIGN -> {
                InformationDetailRewardHolder(parent)
            }
            ICViewTypes.SPONSOR_CAMPAIGN -> {
                SupplierRewardHolder(parent)
            }
            else -> NullHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is HeaderInforRewardHolder -> {
                holder.bind(listData[position].data as ICItemReward)
            }
            is InformationDetailRewardHolder -> {
                holder.bind(listData[position].data as ICItemReward)
            }
            is SupplierRewardHolder -> {
                holder.bind(listData[position].data as ICItemReward)
            }
        }
    }
}