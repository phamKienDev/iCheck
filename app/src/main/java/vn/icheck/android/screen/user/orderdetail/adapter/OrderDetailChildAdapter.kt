package vn.icheck.android.screen.user.orderdetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_order_detail_child.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.util.kotlin.WidgetUtils

class OrderDetailChildAdapter(val listData: MutableList<ICItemCart>) : RecyclerView.Adapter<OrderDetailChildAdapter.ViewHolder>() {

    override fun getItemCount(): Int = listData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail_child, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICItemCart>(view) {

        override fun bind(obj: ICItemCart) {
            WidgetUtils.loadImageUrlRounded(itemView.imgAvatar, obj.thumbnails?.square, WidgetUtils.defaultError, SizeHelper.size5)
            itemView.tvName.text = obj.name
            itemView.tvCount.text = ("x${obj.quantity}")
            itemView.tvProductPrice.text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney((obj.price * obj.quantity)))

            var attributes = ""

            for (atr in obj.attributes ?: mutableListOf()) {
                if (attributes.isNotEmpty()) {
                    attributes += ", " + atr.value
                } else {
                    attributes += atr.value
                }
            }

            itemView.tvAttributes.text = attributes


//            attributes
        }
    }
}