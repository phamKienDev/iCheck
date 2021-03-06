package vn.icheck.android.component.tendency.adapter

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.tendency.ITopTendencyListener
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.models.ICExperienceCategory

class TopTendencyCategoryAdapter(val listener: ITopTendencyListener) : RecyclerView.Adapter<TopTendencyCategoryAdapter.ViewHolder>() {

    val listData = mutableListOf<ICExperienceCategory>()
    var oldPos = -1

    fun setData(list: MutableList<ICExperienceCategory>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICExperienceCategory>(ViewHelper.createHomeCategoryHorizontal(parent.context)) {
        override fun bind(obj: ICExperienceCategory) {
            (itemView as AppCompatTextView).run {
                text = obj.name

                if (obj.isSelected) {
                    oldPos = adapterPosition
                    background = vn.icheck.android.ichecklibs.ViewHelper.bgWhiteCorners16(context)
                    setTextColor(ColorManager.getAccentYellowColor(context))
                } else {
                    background = ViewHelper.createStateListDrawable(
                        ColorManager.getAccentYellowColor(context), ColorManager.getAccentYellowColor(context),
                            ColorManager.getNormalTextColor(context), ColorManager.getNormalTextColor(context),
                            SizeHelper.size1, SizeHelper.size16.toFloat())
                    setTextColor(ColorManager.getNormalTextColor(context))
                }

                setOnClickListener {
                    listData[oldPos].isSelected = false
                    listData[adapterPosition].isSelected = true
                    notifyDataSetChanged()
                    listener.onClickItemCategory(obj.name!!)
                }
            }
        }
    }
}