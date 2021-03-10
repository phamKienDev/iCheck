package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign.adapter

import android.annotation.SuppressLint
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_user_reward_campaign.view.*
import kotlinx.android.synthetic.main.item_user_reward_emty.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICCampaign_User_Reward
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign.view.ITheWinnerCampaignView
import vn.icheck.android.util.kotlin.WidgetUtils

class UserRewardCampaignAdapter constructor(val view: ITheWinnerCampaignView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICCampaign_User_Reward>()

    private var errorCode = ""
    private var isLoadMore = false
    private var isLoading = false

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    fun setListData(list: MutableList<ICCampaign_User_Reward>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = ""

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICCampaign_User_Reward>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = ""

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearListData() {
        errorCode = ""
        isLoadMore = false
        isLoading = false
        listData.clear()
        notifyDataSetChanged()
    }

    fun showLoading() {
        listData.clear()
        errorCode = ""
        isLoadMore = true

        notifyDataSetChanged()
    }

    fun setErrorCode(error: String) {
        listData.clear()
        errorCode = error
        isLoadMore = false

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            itemType -> ViewHolder(inflater.inflate(R.layout.item_user_reward_campaign, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.item_user_reward_emty, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore)
                listData.size + 1
            else
                listData.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            if (position < listData.size)
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
                val item = listData[position]
                holder.bind(item)

                holder.itemView.setOnClickListener {
                    view.onClickItem(item)
                }
            }
            is LoadHolder -> {
                holder.bind()
                if (!isLoading) {
                    view.onLoadMore()
                    isLoading = true
                }
            }
            is MessageHolder -> {
                holder.bind(errorCode)

                holder.itemView.setOnClickListener {
                    view.onRefresh()
                }
            }
        }
    }

    private class ViewHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ICCampaign_User_Reward) {
            val imgReward = item.reward_image
            when (item.type) {
                1 -> {
                    if (imgReward != null && imgReward.isNotEmpty()) {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgGiftUserReward, imgReward)
                    } else {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgGiftUserReward, null)
                    }
                }

                2 -> {
                    if (imgReward != null && imgReward.isNotEmpty()) {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgGiftUserReward, imgReward)
                    } else {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgGiftUserReward, null)
                    }
                }

                3 -> {
                    when (item.reward_name) {
                        "500.000 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_500k_80_dp)
                        }
                        "200.000 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_200k_80_dp)
                        }
                        "100.000 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_100k_80_dp)
                        }
                        "50.000 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_50k_80_dp)
                        }
                        "20.000 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_20k_80_dp)
                        }
                        "10.000 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_10k_80_dp)
                        }
                        "5.000 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_5000_80_dp)
                        }
                        "2.000 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_2000_80_dp)
                        }
                        "1.000 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_1000_80_dp)
                        }
                        "500 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_500_80_dp)
                        }
                        "200 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_200_80_dp)
                        }
                        "100 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_100_80_dp)
                        }
                        "50 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_50_80_dp)
                        }
                        "20 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_20_80_dp)
                        }
                        "10 iCoin" -> {
                            itemView.imgGiftUserReward.setImageResource(R.drawable.ic_icoin_10_80_dp)
                        }
                    }
                }
            }

            if (!item.name.isNullOrEmpty()) {
                itemView.tvNameUser.text = item.name
            } else {
                itemView.tvNameUser.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (!item.receive_at.isNullOrEmpty()) {
                itemView.tvReceive_at.text = item.receive_at
            } else {
                itemView.tvReceive_at.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (!item.phone.isNullOrEmpty()) {
                itemView.tvPhoneUser.text = item.phone
            } else {
                itemView.tvPhoneUser.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (!item.reward_name.isNullOrEmpty()) {
                itemView.tvGiftReward.text = item.reward_name
            } else {
                itemView.tvGiftReward.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (!item.business_name.isNullOrEmpty()) {
                itemView.tvVendor.text = Html.fromHtml("<font color=#828282>Nhà tài trợ: </font>" + "<b>" + item.business_name + "</b>")
            } else {
                itemView.tvVendor.text = itemView.context.getString(R.string.dang_cap_nhat)
            }
        }
    }

    private class LoadHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    private class MessageHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: String) {
            itemView.txtMessage.text = if (errorCode.isNotEmpty())
                errorCode
            else
                itemView.context.getString(R.string.hien_tai_chua_co_nguoi_trung_thuong)
        }
    }

}