package vn.icheck.android.screen.user.gift_campaign.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_campaign.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.models.campaign.ICGiftOfCampaign
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.WidgetUtils

class ItemGiftOfCampaignAdapter(private val listData: MutableList<ICGiftOfCampaign>, private val type: Int) : RecyclerView.Adapter<ItemGiftOfCampaignAdapter.ViewHolder>() {

    var show = 0

    fun setData() {
        show = if (listData.size >= 6) {
            6
        } else {
            listData.size
        }
    }

    fun loadMore() {
        show += if (listData.size >= (show + 8)) {
            8
        } else {
            listData.size - show
        }

        notifyDataSetChanged()
    }

    val showButton: Boolean
        get() {
            return listData.size > (show + 8)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemGiftOfCampaignAdapter.ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return show
    }

    override fun onBindViewHolder(holder: ItemGiftOfCampaignAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICGiftOfCampaign>(LayoutInflater.from(parent.context).inflate(R.layout.item_gift_campaign, parent, false)) {

        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICGiftOfCampaign) {
            obj.rewardTotal?.let {
                itemView.tvCountGift.setText(R.string.d_qua, it)
            }
            itemView.tvPrice.background=ViewHelper.bgGrayCornersTopRight12BottomRight12(itemView.context)

            when (type) {
                ICViewTypes.PRODUCT_CAMPAIGN_TYPE -> {
                    WidgetUtils.loadImageUrl(itemView.imgICoin, obj.image)
                    WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.logo, R.drawable.ic_business_v2)

                    itemView.imgAvatar.layoutParams = ConstraintLayout.LayoutParams(SizeHelper.size32, SizeHelper.size32).also {
                        it.topMargin = SizeHelper.size10
                    }
                    itemView.tvPrice.setText(R.string.s_d, TextHelper.formatMoneyPhay(obj.rewardValue))
                }
                ICViewTypes.VOUCHER_CAMPAIGN_TYPE -> {
                    WidgetUtils.loadImageUrl(itemView.imgICoin, obj.image)
                    WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.logo, R.drawable.ic_business_v2)

                    itemView.tvPrice.setText(R.string.s_d, TextHelper.formatMoneyPhay(obj.rewardValue))
                }
                ICViewTypes.ICOIN_CAMPAIGN_TYPE -> {
                    itemView.tvName.visibility = View.GONE
                    itemView.imgAvatar.setImageResource(R.drawable.ic_icheck_logo)

                    WidgetUtils.loadImageUrlFitCenter(itemView.imgICoin, obj.icoinIcon, R.drawable.ic_icheck_xu)

                    itemView.imgAvatar.layoutParams = ConstraintLayout.LayoutParams(SizeHelper.size32, SizeHelper.size32).also {
                        it.topMargin = SizeHelper.size16
                    }
                    itemView.tvPrice.setText(R.string.s_xu, TextHelper.formatMoneyPhay(obj.icoin))
                }
                ICViewTypes.MORALE_CAMPAIGN_TYPE -> {
                    WidgetUtils.loadImageUrl(itemView.imgICoin, obj.image)

                    itemView.tvPrice.visibility = View.GONE
                    itemView.imgAvatar.visibility = View.GONE
                }
                ICViewTypes.CAMPAIGN_365_TYPE -> {
                    WidgetUtils.loadImageUrl(itemView.imgICoin, obj.image)

                    itemView.tvPrice.visibility = View.GONE
                    itemView.imgAvatar.visibility = View.GONE
                }
            }

            itemView.tvName.text = if (obj.name.isNullOrEmpty()) {
                getString(R.string.dang_cap_nhat)
            } else {
                obj.name
            }
        }
    }
}