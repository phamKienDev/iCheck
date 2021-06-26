package vn.icheck.android.screen.user.newslistv2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.databinding.ItemNewCategoryBinding
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.loyalty.base.listener.IClickListener
import vn.icheck.android.network.models.ICArticleCategory

class NewCategoryAdapter(val listData: MutableList<ICArticleCategory>) : RecyclerView.Adapter<NewCategoryAdapter.ViewHolder>() {
    var clickListener: IClickListener? = null

    fun unCheckAll() {
        for (item in listData) {
            if (item.isChecked) {
                item.isChecked = false
            }
        }
        notifyDataSetChanged()
    }

    fun setListener(listener: IClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewCategoryAdapter.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NewCategoryAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(parent: ViewGroup, val binding: ItemNewCategoryBinding = ItemNewCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICArticleCategory>(binding.root) {
        override fun bind(obj: ICArticleCategory) {
            if (!obj.name.isNullOrEmpty()) {
                binding.tvChecked.apply {
                    background = ViewHelper.createDrawableStateList(ContextCompat.getDrawable(itemView.context,R.drawable.bg_gray_f0_corners_4), vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(itemView.context))
                    setTextColor(ViewHelper.createColorStateList(ColorManager.getNormalTextColor(itemView.context), ContextCompat.getColor(itemView.context, R.color.white)))
                    text = obj.name

                    isChecked = obj.isChecked

                    setOnClickListener {
                        unCheckAll()
                        obj.isChecked = true
                        clickListener?.onClick(obj)
                        notifyItemChanged(adapterPosition)
                    }
                }
            }
        }
    }
}