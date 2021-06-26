package vn.icheck.android.screen.user.checkoutcart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_checkout_shipping.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICShipping

class SelectShippingAdapter(val shippingID: Int, val listData: MutableList<ICShipping>) : RecyclerView.Adapter<SelectShippingAdapter.ViewHolder>() {
    private var selectedPosition = -1

    val getSelectedShipping: ICShipping?
        get() {
            return if (selectedPosition != -1) {
                listData[selectedPosition]
            } else {
                null
            }
        }

    override fun getItemCount(): Int = listData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_checkout_shipping, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICShipping>(view) {

        override fun bind(obj: ICShipping) {
            itemView.tvName.text = if (obj.shipping_amount > 0L) {
                itemView.context.getString(R.string.xxx_xxx, obj.method.name, itemView.context.getString(R.string.s_d, TextHelper.formatMoney(obj.shipping_amount)))
            } else {
                obj.method.name
            }

            if (selectedPosition == -1) {
                if (obj.id == shippingID) {
                    selectedPosition = adapterPosition
                }
            }

            if (selectedPosition == adapterPosition) {
                itemView.tvChecked.setImageResource(R.drawable.ic_radio_checked_blue_24dp)
                itemView.tvNote.visibility = View.VISIBLE
                itemView.tvNote.text = obj.name

                if (adapterPosition == 0) {
                    itemView.viewTop.visibility = View.INVISIBLE
                    itemView.viewBottom.visibility = if (listData.size == 1) {
                        View.INVISIBLE
                    } else {
                        View.VISIBLE
                    }
                } else if (adapterPosition == listData.size - 1) {
                    itemView.viewTop.visibility = View.VISIBLE
                    itemView.viewBottom.visibility = View.INVISIBLE
                } else {
                    itemView.viewTop.visibility = View.VISIBLE
                    itemView.viewBottom.visibility = View.VISIBLE
                }
            } else {
                itemView.tvChecked.setImageResource(R.drawable.ic_radio_un_checked_gray_24dp)
                itemView.tvNote.visibility = View.GONE

                itemView.viewTop.visibility = View.INVISIBLE
                itemView.viewBottom.visibility = View.INVISIBLE
            }

            itemView.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged()
            }
        }
    }
}