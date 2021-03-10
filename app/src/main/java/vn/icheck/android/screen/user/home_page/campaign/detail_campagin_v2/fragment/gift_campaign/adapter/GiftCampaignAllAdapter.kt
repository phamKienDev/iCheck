package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.gift_campaign.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_campaign.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.util.kotlin.WidgetUtils

class GiftCampaignAllAdapter(val listData: MutableList<ICCampaign>, val type: Int) : RecyclerView.Adapter<GiftCampaignAllAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICCampaign>(LayoutInflater.from(parent.context).inflate(R.layout.item_gift_campaign, parent, false)) {
        override fun bind(obj: ICCampaign) {

            //1 Quà Hiện vật
            //2 Quà iCoin
            //3 Quà tinh thần
            when (type) {
                1 -> {
                    itemView.layoutCount.visibility = View.VISIBLE
                    itemView.tvPrice.text = "${TextHelper.formatMoneyPhay(obj.price)}đ"
                    itemView.tvCountGift.text = "${obj.total} Quà"
                }
                2 -> {
                    itemView.tvName.visibility = View.GONE
                    itemView.imgAvatar.layoutParams = ConstraintLayout.LayoutParams(SizeHelper.size32, SizeHelper.size32).also {
                        it.topMargin = 16
                    }
                    itemView.tvPrice.text = "${TextHelper.formatMoneyPhay(obj.icoin)} Xu"
                }
                3 -> {
                    itemView.layoutCount.visibility = View.GONE
                    itemView.tvPrice.visibility = View.GONE
                    itemView.imgAvatar.visibility = View.GONE
                }
            }

            WidgetUtils.loadImageUrl(itemView.imgICoin, obj.image)
            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.logo, R.drawable.img_default_shop_logo)

            itemView.tvName.text = obj.title

        }
    }
}