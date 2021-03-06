package vn.icheck.android.screen.dialog.report

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.models.product.report.ICReportForm

class ItemReportSuccessAdapter(val listData: MutableList<ICReportForm>) : RecyclerView.Adapter<ItemReportSuccessAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICReportForm>(createView(parent.context)) {

        override fun bind(obj: ICReportForm) {
            (itemView as AppCompatTextView).text = obj.name ?: obj.description
        }

        companion object {

            fun createView(context: Context): AppCompatTextView {
                return ViewHelper.createText(context,
                        ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0, SizeHelper.size4, SizeHelper.size6, SizeHelper.size4),
                            ViewHelper.createShapeDrawable(ContextCompat.getColor(context, vn.icheck.android.ichecklibs.R.color.grayF0), SizeHelper.size32.toFloat()),
                        ViewHelper.createTypeface(context, R.font.barlow_medium),
                        ColorManager.getNormalTextColor(context),
                        14f).also {
                    it.setPadding(SizeHelper.size16, SizeHelper.size6, SizeHelper.size16, SizeHelper.size6)
                }
            }
        }
    }
}