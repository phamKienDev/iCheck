package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.databinding.ItemRowCheckboxFieldBinding
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ValueFItem

class CheckBoxFieldAdapter (val listData: MutableList<ValueFItem>) : RecyclerView.Adapter<CheckBoxFieldAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    class ViewHolder(parent: ViewGroup, val binding: ItemRowCheckboxFieldBinding =
            ItemRowCheckboxFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ValueFItem) {
            binding.tvCheck.apply {
                setTextColor(ViewHelper.textColorNormalCheckedSecondUnchecked(itemView.context))
                text = item.value
                isChecked = item.isChecked

                val drawable=ViewHelper.createCheckedDrawable(
                    ContextCompat.getDrawable(itemView.context,R.drawable.ic_checkbox_unfc_24dp),
                    ViewHelper.fillDrawableColor(ContextCompat.getDrawable(itemView.context,R.drawable.ic_checkbox_on_24dp)))
                setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
            }

            binding.tvCheck.setOnClickListener {
                item.isChecked =! item.isChecked
                binding.tvCheck.isChecked = item.isChecked
            }
        }
    }

}