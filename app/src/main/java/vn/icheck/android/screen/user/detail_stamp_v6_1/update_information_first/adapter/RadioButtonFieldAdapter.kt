package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_radio_button_field.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.detail_stamp_v6_1.ValueFItem

class RadioButtonFieldAdapter(val listData: MutableList<ValueFItem>) : RecyclerView.Adapter<RadioButtonFieldAdapter.ViewHolder>() {
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_radio_button_field, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ValueFItem) {
            itemView.tvRadio.text = item.value

            itemView.tvRadio.isChecked = item.isChecked
            if (item.isChecked) {
                checkedPosition == adapterPosition
            }

            itemView.setOnClickListener {
                itemView.tvRadio.isChecked = true

                if (checkedPosition != adapterPosition && checkedPosition != -1) {
                    listData[checkedPosition].isChecked = false
                    notifyItemChanged(checkedPosition)
                }
                checkedPosition = adapterPosition
                listData[checkedPosition].isChecked = true
            }
        }
    }

}