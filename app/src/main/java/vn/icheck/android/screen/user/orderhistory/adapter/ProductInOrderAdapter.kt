package vn.icheck.android.screen.user.orderhistory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product_in_order.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.OrderItemItem
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductInOrderAdapter : RecyclerViewAdapter<OrderItemItem>() {

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_in_order, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    private inner class ViewHolder(view: View) : BaseViewHolder<OrderItemItem>(view) {

        @Suppress("DEPRECATION")
        override fun bind(obj: OrderItemItem) {
            WidgetUtils.loadImageUrlRounded(itemView.imgProduct, obj.productInfo?.imageUrl,R.drawable.img_default_product_big, SizeHelper.size4)
            itemView.tvProduct.text = obj.productInfo?.name
            itemView.tvCount.text = "x ${obj.quantity ?: 1}"
            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    obj.id?.let { id -> ShipActivity.startDetailOrder(it, id) }
                }
            }
        }
    }
}