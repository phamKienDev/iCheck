package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_row_checkbox_field.view.*
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICServiceShopVariant
import vn.icheck.android.network.models.detail_stamp_v6_1.ValueFItem

class CheckBoxFieldAdapter (val listData: MutableList<ValueFItem>) : RecyclerView.Adapter<CheckBoxFieldAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_row_checkbox_field, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ValueFItem) {
            itemView.tvCheck.setTextColor(ViewHelper.textColorNormalCheckedSecondUnchecked(itemView.context))
            itemView.tvCheck.text = item.value

            itemView.tvCheck.isChecked = item.isChecked

            itemView.setOnClickListener {
                item.isChecked =! item.isChecked
                itemView.tvCheck.isChecked = item.isChecked
            }
        }
    }

}