package vn.icheck.android.loyalty.screen.loyalty_customers.campaign_of_business

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_campaign_of_business.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.TimeHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKCampaignOfBusiness
import vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail.LoyaltyVipDetailActivity

internal class CampaignOfBusinessAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<ICKCampaignOfBusiness>(callback) {

    override fun getItemType(position: Int): Int {
        return ICKViewType.ITEM_TYPE
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKCampaignOfBusiness>(R.layout.item_campaign_of_business, parent) {

        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICKCampaignOfBusiness) {
            WidgetHelper.loadImageUrl(itemView.imgBanner, obj.image?.original, R.drawable.ic_default_img_game)

            itemView.tvTime.text = TimeHelper.convertDateTimeSvToDateVn(obj.start_at) + " - " + TimeHelper.convertDateTimeSvToDateVn(obj.end_at)

            when (obj.customer_status?.code) {
                "COMING_SOON" -> {
                    itemView.imgUpcoming.setVisible()
                    itemView.layoutRight.setGone()
                    itemView.tvChuaThamGia.setGone()
                }
                "JOINED" -> {
                    itemView.imgUpcoming.setGone()
                    itemView.layoutRight.setVisible()
                    itemView.tvChuaThamGia.setGone()
                }
                "NOTHING" -> {
                    itemView.tvChuaThamGia.setVisible()
                    itemView.imgUpcoming.setGone()
                    itemView.layoutRight.setGone()
                }
                else -> {
                    itemView.tvChuaThamGia.setGone()
                    itemView.imgUpcoming.setGone()
                    itemView.layoutRight.setGone()
                }
            }

            itemView.setOnClickListener {
                SharedLoyaltyHelper(itemView.context).putBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_POINT_LONG_TIME, obj.has_chance_code ?: false)
                itemView.context.startActivity(Intent(itemView.context, LoyaltyVipDetailActivity::class.java).apply {
                    putExtra(ConstantsLoyalty.DATA_3, 1)
                    putExtra(ConstantsLoyalty.DATA_2, obj)
                    putExtra(ConstantsLoyalty.DATA_1, obj.id)
                })
            }
        }
    }
}