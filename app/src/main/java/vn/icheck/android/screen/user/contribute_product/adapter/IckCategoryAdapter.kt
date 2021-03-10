package vn.icheck.android.screen.user.contribute_product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.databinding.ItemIckCategoryBinding
import vn.icheck.android.model.category.CategoryItem
import vn.icheck.android.util.ick.simpleText

class IckCategoryAdapter(private val onClick: (category: CategoryItem?) -> Unit):PagingDataAdapter<CategoryItem, RecyclerView.ViewHolder>(CATEGORY_DIFF) {

    companion object{
        val CATEGORY_DIFF = object : DiffUtil.ItemCallback<CategoryItem>() {
            override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
                return  oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
                return  oldItem.id == newItem.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = LayoutInflater.from(parent.context)
        val binding = ItemIckCategoryBinding.inflate(lf, parent, false)
        return IckCategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as IckCategoryHolder
        holder.binding.tvCategoryName simpleText  getItem(position)?.name
        if (getItem(position)?.childrenCount == null) {
            holder.binding.tvCategoryName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            if (getItem(position)?.childrenCount!! > 0) {
                holder.binding.tvCategoryName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_light_blue_24dp, 0)
            } else {
                holder.binding.tvCategoryName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
        holder.binding.root.setOnClickListener {
            onClick(getItem(position))
        }
    }

    class IckCategoryHolder(val binding: ItemIckCategoryBinding):RecyclerView.ViewHolder(binding.root)
}