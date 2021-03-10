package vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.msp_holder.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.base.ICKViewType
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.TopupServices

internal class ChangePhoneCardsAdapter : RecyclerViewCustomAdapter<TopupServices.Service>() {
    private var click: ITopUpServiceListener? = null

    private var selectedPosition = -1

    fun setData(obj: List<TopupServices.Service>) {
        listData.clear()

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun setListener(listener: ITopUpServiceListener) {
        click = listener
    }

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

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<TopupServices.Service>(R.layout.msp_holder, parent) {
        override fun bind(obj: TopupServices.Service) {
            WidgetHelper.loadImageFitCenterUrl(itemView.imgIcon, obj.avatar, R.drawable.ic_default_square)

            checkSelected()

            itemView.setOnClickListener {
                if (selectedPosition != adapterPosition) {
                    selectedPosition = adapterPosition
                    click?.onSelected(obj)
                    notifyDataSetChanged()
                }
            }
        }

        private fun checkSelected() {
            if (selectedPosition == adapterPosition) {
                itemView.layoutContent.setBackgroundResource(R.drawable.bg_choose_card_loyalty)
            } else {
                itemView.layoutContent.setBackgroundResource(R.drawable.bg_default_card_loyalty)
            }
        }
    }

    interface ITopUpServiceListener {
        fun onSelected(obj: TopupServices.Service)
    }
}