package vn.icheck.android.screen.user.contribute_product.dialog

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.databinding.ItemCategoryTitleBinding
import vn.icheck.android.network.model.category.CategoryItem
import vn.icheck.android.util.ick.*

class CategoryHeadlineAdapter(val onClick:(Int) -> Unit, val listCategory:List<CategoryItem?>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = parent.getLayoutInflater()
        val bd = ItemCategoryTitleBinding.inflate(lf, parent, false)
        return TitleHolder(bd)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TitleHolder).binding.root.setOnClickListener {
            onClick.invoke(position)
        }
        when (position) {
            0 -> {
                holder.binding.imgArr.beGone()
                holder.binding.tvTitle.setText(R.string.tat_ca)
            }
            listCategory.lastIndex -> {
                holder.binding.tvTitle simpleText listCategory[position]?.name
                holder.binding.tvTitle.setTextColor(Color.parseColor("#057DDA"))
            }
            else -> {
                holder.binding.tvTitle simpleText listCategory[position]?.name
                holder.binding.tvTitle.setTextColor(Color.parseColor("#757575"))
            }
        }
    }

    override fun getItemCount() = listCategory.size

    inner class TitleHolder(val binding:ItemCategoryTitleBinding):RecyclerView.ViewHolder(binding.root)
}