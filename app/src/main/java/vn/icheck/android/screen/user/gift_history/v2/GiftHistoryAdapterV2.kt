package vn.icheck.android.screen.user.gift_history.v2

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_header_gift_campaign.view.*
import kotlinx.android.synthetic.main.layout_gift_recevied_history_holder.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.base.ICKViewType
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.screen.user.coinhistory.CoinHistoryActivity
import vn.icheck.android.screen.user.detail_my_reward.DetailMyRewardActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.simpleText
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.net.URL

class GiftHistoryAdapterV2(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<Any>(callback) {

    fun setHeaderImage(obj: String) {
        listData.clear()

        if (listData.isNullOrEmpty()) {
            listData.add(obj)
        } else {
            listData.add(0, obj)
        }
        notifyDataSetChanged()
    }

    fun setData(obj: MutableList<ICItemReward>) {
        checkLoadMore(obj.size)

        for (i in listData.size - 1 downTo 0) {
            if (listData[i] is ICItemReward) {
                listData.removeAt(i)
            }
        }
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: MutableList<ICItemReward>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position] is String) {
            ICKViewType.HEADER_TYPE
        } else {
            ICKViewType.ITEM_TYPE
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ICKViewType.HEADER_TYPE) {
            HeaderViewHolder(parent)
        } else {
            ViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is HeaderViewHolder -> {
                if (listData[position] is String) {
                    holder.bind(listData[position] as String)
                }
            }
            is ViewHolder -> {
                if (listData[position] is ICItemReward) {
                    holder.bind(listData[position] as ICItemReward)
                }
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    inner class HeaderViewHolder(parent: ViewGroup) : BaseViewHolder<String>(R.layout.item_header_gift_campaign, parent) {
        override fun bind(obj: String) {
            WidgetUtils.loadImageUrl(itemView.imgBanner, obj, R.drawable.bg_error_campaign)
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICItemReward>(R.layout.layout_gift_recevied_history_holder, parent) {
        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICItemReward) {

            WidgetUtils.loadImageUrl(itemView.imgLogoPage, obj.logo, R.drawable.ic_business_v2)

            itemView.tvNamePage.text = TimeHelper.convertDateTimeSvToTimeDateVnPhay(obj.receiveAt)

            if (obj.value != null) {
                itemView.tvName.text = "${TextHelper.formatMoneyPhay(obj.value)} Xu"

                when (obj.value) {
                    10L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_10_icoin)
                    }
                    20L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_20_icoin)
                    }
                    50L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_50_icoin)
                    }
                    100L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_100_icoin)
                    }
                    200L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_200_icoin)
                    }
                    500L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_500_icoin)
                    }
                    1000L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_1000_icoin)
                    }
                    2000L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_2000_icoin)
                    }
                    5000L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_5000_icoin)
                    }
                    10000L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_10000_icoin)
                    }
                    20000L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_20000_icoin)
                    }
                    50000L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_50000_icoin)
                    }
                    100000L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_100000_icoin)
                    }
                    200000L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_200000_icoin)
                    }
                    500000L -> {
                        itemView.imgGift.setImageResource(R.drawable.ic_500000_icoin)
                    }
                }

                itemView.tvAction simpleText "Quản lý Xu"
                itemView.tvAction.setOnClickListener {
                    ICheckApplication.currentActivity()?.let { activity ->
                        ActivityUtils.startActivity<CoinHistoryActivity>(activity)
                    }
                }
            } else {

                if (obj.rewardType == "CODE") {
                    checkNullOrEmpty(itemView.tvName, "${obj.name} - ${obj.code}")
                } else {
                    checkNullOrEmpty(itemView.tvName, obj.name)
                }

                WidgetUtils.loadImageUrl(itemView.imgGift, obj.image)
                itemView.tvAction simpleText "Xem chi tiết"
                itemView.tvAction.setOnClickListener {
                    ICheckApplication.currentActivity()?.let { activity ->
                        if (obj.rewardType == "CODE") {
                            if (!obj.landingCode.isNullOrEmpty()) {

                                val url = Uri.parse(if (obj.landingCode!!.contains("https://", true) || obj.landingCode!!.contains("http://", true)) {
                                    obj.landingCode
                                } else {
                                    "https://${obj.landingCode}"
                                })
                                        .buildUpon()
                                        .appendQueryParameter("code", obj.code ?: "")
                                        .build()
                                WebViewActivity.start(activity, url.toString(), title = obj.name)
                            }
                        } else {
                            ActivityUtils.startActivity<DetailMyRewardActivity, String>(activity, Constant.DATA_1, obj.id ?: "")
                        }
                    }
                }
            }
        }
    }
}