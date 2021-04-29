package vn.icheck.android.screen.user.gift_campaign.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_campain_all.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.screen.user.gift_campaign.GiftOfCampaignModel
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils

class GiftOfCampaignAdapter(callback: IRecyclerViewCallback, val banner: String?) : RecyclerViewCustomAdapter<GiftOfCampaignModel>(callback) {

    fun addGiftProduct(obj: MutableList<GiftOfCampaignModel>) {
        listData.clear()

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return ICViewTypes.CAMPAIGN_TYPE
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(listData[position])
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<GiftOfCampaignModel>(LayoutInflater.from(parent.context).inflate(R.layout.item_gift_campain_all, parent, false)) {
        @SuppressLint("SetTextI18n")
        override fun bind(obj: GiftOfCampaignModel) {

            val adapter = ItemGiftOfCampaignAdapter(obj.data, obj.type)

            WidgetUtils.loadImageUrl(itemView.imgHeader, banner)

            itemView.imgHeader.run {
                if (adapterPosition == 0) {
                    beVisible()
                } else {
                    beGone()
                }
            }

            itemView.view.run {
                if (adapterPosition == listData.size - 1) {
                    beGone()
                } else {
                    beVisible()
                }
            }

            itemView.tvTitle.text = obj.title

            itemView.recyclerView.layoutManager = CustomGridLayoutManager(itemView.context, 2)
            itemView.recyclerView.adapter = adapter
            adapter.setData()

            if (obj.data.size > 6) {
                itemView.btnMore.beVisible()

                itemView.btnMore.setOnClickListener {
                    if (adapter.show != obj.data.size) {
                        adapter.loadMore()
                        itemView.btnMore.run {
                            text = if (adapter.showButton) {
                                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_light_blue_accent_24px, 0)
                                "Xem thêm"
                            } else {
                                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_blue_24dp, 0)
                                "Thu gọn"
                            }
                        }
                    } else {
                        adapter.setData()
                        adapter.notifyDataSetChanged()
                        itemView.btnMore.run {
                            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_light_blue_accent_24px, 0)
                            text = "Xem thêm"
                        }
                    }
                }
            } else {
                itemView.btnMore.beGone()
            }
        }
    }
}