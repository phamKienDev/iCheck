package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_reward_campaign.view.*
import kotlinx.android.synthetic.main.layout_gift_message.view.*
import vn.icheck.android.R
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICCampaign_Reward
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign.view.IGiftCampaignView
import vn.icheck.android.util.kotlin.WidgetUtils

class RewardCampaignAdapter constructor(val view: IGiftCampaignView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    private var errorCode = ""

    private var isLoadMore = false
    private var isLoading = false

    val listCondition = mutableListOf<ICCampaign_Reward>()

    private fun checkLoadMore(listCount: Int) {
        isLoading = false
        isLoadMore = listCount >= APIConstants.LIMIT
        errorCode = ""
    }

    fun setListData(list: MutableList<ICCampaign_Reward>) {
        checkLoadMore(list.size)
        listCondition.clear()
        listCondition.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICCampaign_Reward>) {
        checkLoadMore(list.size)
        listCondition.addAll(list)
        notifyDataSetChanged()
    }

    fun setErrorCode(error: String) {
        listCondition.clear()
        errorCode = error
        isLoadMore = false
        isLoading = false
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listCondition.isEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_reward_campaign, parent, false))
            loadType -> LoadHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false))
            else -> ErrorHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_gift_message, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listCondition.size > 0) {
            if (isLoadMore)
                listCondition.size + 1
            else
                listCondition.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listCondition.size > 0) {
            if (position < listCondition.size)
                itemType
            else
                loadType
        } else {
            if (isLoadMore)
                loadType
            else
                showType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listCondition[position]
                holder.setData(item)
            }

            is LoadHolder -> {
                holder.bind()
                if (!isLoading) {
                    view.onLoadMore()
                    isLoading = true
                }
            }
            is ErrorHolder -> {
                holder.setData(errorCode)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(item: ICCampaign_Reward) {
            val image = item.image

            when (item.type) {
                1 -> {
                    if (image != null && image.isNotEmpty()) {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgAvaReward, image)
                    } else {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgAvaReward, null)
                    }
                }
                2 -> {
                    if (image != null && image.isNotEmpty()) {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgAvaReward, image)
                    } else {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgAvaReward, null)
                    }
                }
                3 -> {
                    when (item.name) {
                        "500.000 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_500k_80_dp)
                        }
                        "200.000 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_200k_80_dp)
                        }
                        "100.000 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_100k_80_dp)
                        }
                        "50.000 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_50k_80_dp)
                        }
                        "20.000 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_20k_80_dp)
                        }
                        "10.000 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_10k_80_dp)
                        }
                        "5.000 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_5000_80_dp)
                        }
                        "2.000 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_2000_80_dp)
                        }
                        "1.000 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_1000_80_dp)
                        }
                        "500 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_500_80_dp)
                        }
                        "200 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_200_80_dp)
                        }
                        "100 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_100_80_dp)
                        }
                        "50 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_50_80_dp)
                        }
                        "20 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_20_80_dp)
                        }
                        "10 iCoin" -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_icoin_10_80_dp)
                        }
                        else -> {
                            itemView.imgAvaReward.setImageResource(R.drawable.ic_coin_status_24dp)
                        }
                    }
                }
            }

            itemView.tvNameReward.text = item.name
            itemView.tvBussinessName.text = item.business_name
            WidgetUtils.loadImageUrl(itemView.imgAva,item.logo)
        }
    }

    private class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    class ErrorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(errorCode: String) {
            itemView.txtMessage.text = if (errorCode.isNotEmpty())
                errorCode
            else
                itemView.context.getString(R.string.hien_tai_khong_co_qua_tang_nao)
        }
    }

}