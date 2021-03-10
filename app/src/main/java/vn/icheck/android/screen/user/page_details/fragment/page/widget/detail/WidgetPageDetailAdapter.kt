package vn.icheck.android.screen.user.page_details.fragment.page.widget.detail

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICPageInformations
import vn.icheck.android.screen.bottomsheet.BottomSheetInformationPage
import vn.icheck.android.screen.bottomsheet.BottomSheetPrizePage
import vn.icheck.android.util.kotlin.WidgetUtils

class WidgetPageDetailAdapter(val listData: MutableList<ICPageInformations>) : RecyclerView.Adapter<WidgetPageDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetPageDetailAdapter.ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: WidgetPageDetailAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICPageInformations>(ViewHelper.createItemInformationPage(parent.context)) {
        override fun bind(obj: ICPageInformations) {

            (itemView as LinearLayout).run {

                setOnClickListener {
                    if (adapterPosition == 0) {
                        object : BottomSheetInformationPage(itemView.context, obj) {

                        }.onShow()
                    } else if (adapterPosition == 1) {
                        object : BottomSheetPrizePage(itemView.context, obj) {

                        }.onShow()
                    }
                }

                layoutParams = if (listData.size < 2) {
                    ViewHelper.createLayoutParams().also {
                        it.setMargins(SizeHelper.size12, 0, SizeHelper.size12, 0)
                    }
                } else {
                    ViewHelper.createLayoutParams(SizeHelper.size259, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                        it.setMargins(SizeHelper.size12, 0, 0, 0)
                    }
                }

                WidgetUtils.loadImageUrl(getChildAt(0) as AppCompatImageView, obj.avatar)

                (getChildAt(1) as LinearLayout).run {

                    (getChildAt(0) as AppCompatTextView).run {
                        if (!obj.name.isNullOrEmpty()) {
                            text = obj.name
                        }
                    }

                    (getChildAt(1) as AppCompatTextView).run {
                        if (!obj.description.isNullOrEmpty()) {
                            text = obj.description
                        }
                    }
                }
            }
        }
    }
}