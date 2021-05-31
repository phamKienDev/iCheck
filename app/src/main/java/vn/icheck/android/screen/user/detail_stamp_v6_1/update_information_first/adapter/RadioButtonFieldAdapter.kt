package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.databinding.ItemRadioButtonFieldBinding
import vn.icheck.android.network.models.detail_stamp_v6_1.ValueFItem

class RadioButtonFieldAdapter(val listData: MutableList<ValueFItem>) : RecyclerView.Adapter<RadioButtonFieldAdapter.ViewHolder>() {
    private var checkedPosition = -1

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
            binding.tvRadio.text = item.value

            binding.tvRadio.isChecked = item.isChecked
            if (item.isChecked) {
                checkedPosition = adapterPosition
            }

            itemView.setOnClickListener {
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