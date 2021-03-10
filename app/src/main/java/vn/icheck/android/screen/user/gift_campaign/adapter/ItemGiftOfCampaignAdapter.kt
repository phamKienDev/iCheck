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
import vn.icheck.android.network.models.campaign.ICGiftOfCampaign
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

            itemView.tvCountGift.text = "${obj.rewardTotal} Quà"

            when (type) {
                ICViewTypes.PRODUCT_CAMPAIGN_TYPE -> {
                    WidgetUtils.loadImageUrl(itemView.imgICoin, obj.image)
                    WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.logo, R.drawable.img_default_business_logo)

                    itemView.imgAvatar.layoutParams = ConstraintLayout.LayoutParams(SizeHelper.size32, SizeHelper.size32).also {
                        it.topMargin = SizeHelper.size10
                    }
                    itemView.tvPrice.text = "${TextHelper.formatMoneyPhay(obj.rewardValue)}đ"
                }
                ICViewTypes.VOUCHER_CAMPAIGN_TYPE -> {
                    WidgetUtils.loadImageUrl(itemView.imgICoin, obj.image)
                    WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.logo, R.drawable.img_default_business_logo)

                    itemView.tvPrice.text = "${TextHelper.formatMoneyPhay(obj.rewardValue)}đ"
                }
                ICViewTypes.ICOIN_CAMPAIGN_TYPE -> {
                    itemView.tvName.visibility = View.GONE
                    itemView.imgAvatar.setImageResource(R.drawable.ic_icheck_logo)

                    itemView.imgICoin.setImageResource(when (obj.icoin) {
                        10L -> {
                            R.drawable.ic_10_icoin
                        }
                        20L -> {
                            R.drawable.ic_20_icoin
                        }
                        50L -> {
                            R.drawable.ic_50_icoin
                        }
                        100L -> {
                            R.drawable.ic_100_icoin
                        }
                        200L -> {
                            R.drawable.ic_200_icoin
                        }
                        500L -> {
                            R.drawable.ic_500_icoin
                        }
                        1000L -> {
                            R.drawable.ic_1000_icoin
                        }
                        2000L -> {
                            R.drawable.ic_2000_icoin
                        }
                        5000L -> {
                            R.drawable.ic_5000_icoin
                        }
                        10000L -> {
                            R.drawable.ic_10000_icoin
                        }
                        20000L -> {
                            R.drawable.ic_20000_icoin
                        }
                        50000L -> {
                            R.drawable.ic_50000_icoin
                        }
                        100000L -> {
                            R.drawable.ic_100000_icoin
                        }
                        200000L -> {
                            R.drawable.ic_200000_icoin
                        }
                        500000L -> {
                            R.drawable.ic_500000_icoin
                        }
                        else -> R.drawable.ic_default_square
                    })

                    itemView.imgAvatar.layoutParams = ConstraintLayout.LayoutParams(SizeHelper.size32, SizeHelper.size32).also {
                        it.topMargin = SizeHelper.size16
                    }
                    itemView.tvPrice.text = "${TextHelper.formatMoneyPhay(obj.icoin)} Xu"
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