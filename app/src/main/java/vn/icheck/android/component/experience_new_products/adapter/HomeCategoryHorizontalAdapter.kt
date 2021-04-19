package vn.icheck.android.component.experience_new_products.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.experience_new_products.IExperienceNewProducts
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICExperienceCategory

class HomeCategoryHorizontalAdapter(val listener: IExperienceNewProducts) : RecyclerView.Adapter<HomeCategoryHorizontalAdapter.ViewHolder>() {
    val listData = mutableListOf<ICExperienceCategory>()
    var oldPos = -1

    fun setData(list: MutableList<ICExperienceCategory>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryHorizontalAdapter.ViewHolder {
        return ViewHolder(ViewHelper.createHomeCategoryHorizontal(parent.context))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: HomeCategoryHorizontalAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICExperienceCategory>(view) {
        override fun bind(obj: ICExperienceCategory) {
            (itemView as AppCompatTextView).run {
                if (!obj.name.isNullOrEmpty()) {
                    text = obj.name

                    if (obj.isSelected) {
                        oldPos = adapterPosition
                        background = ContextCompat.getDrawable(context, R.drawable.bg_category_click_light_blue)
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                    } else {
                        background = ViewHelper.createStateListDrawable(
                                Color.WHITE, ContextCompat.getColor(context, R.color.colorSecondText),
                                ContextCompat.getColor(context, R.color.colorSecondText), ContextCompat.getColor(context, R.color.colorSecondText),
                                SizeHelper.size1, SizeHelper.size16.toFloat())
                        setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                    }

                    setOnClickListener {
                        listData[oldPos].isSelected = false
                        listData[adapterPosition].isSelected = true
                        notifyDataSetChanged()
                        listener.onClickItemCategory(obj.id)
                    }
                }
            }
        }
    }
}