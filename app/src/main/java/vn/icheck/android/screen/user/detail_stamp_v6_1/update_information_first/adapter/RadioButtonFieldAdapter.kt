package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.adapter

import android.content.ClipData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.databinding.ItemRadioButtonFieldBinding
import kotlinx.android.synthetic.main.item_radio_button_field.view.*
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ValueFItem

class RadioButtonFieldAdapter(val listData: MutableList<ValueFItem>, var checkedPosition: Int = -1) : RecyclerView.Adapter<RadioButtonFieldAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)


    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    inner class ViewHolder(parent: ViewGroup, val binding: ItemRadioButtonFieldBinding =
            ItemRadioButtonFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ValueFItem) {
            binding.tvRadio.setTextColor(ViewHelper.textColorNormalCheckedSecondUnchecked(itemView.context))
            binding.tvRadio.text = item.value

            binding.tvRadio.isChecked = item.isChecked
            if (item.isChecked) {
                checkedPosition = adapterPosition
            }

            binding.tvRadio.setOnClickListener {
                binding.tvRadio.isChecked = true

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