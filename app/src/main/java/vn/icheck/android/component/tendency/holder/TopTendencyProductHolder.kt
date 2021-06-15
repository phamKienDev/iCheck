package vn.icheck.android.component.tendency.holder

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.WidgetUtils

class TopTendencyProductHolder(parent: ViewGroup) : BaseViewHolder<ICProductTrend>(ViewHelper.createItemProductTopTendency(parent.context)) {
    override fun bind(obj: ICProductTrend) {
        val parent = itemView as LinearLayout
        val img = parent.getChildAt(0) as AppCompatImageView
        val constraintLayout = parent.getChildAt(1) as ConstraintLayout
        val imgAvt = constraintLayout.getChildAt(0) as CircleImageView
        val tvName = constraintLayout.getChildAt(1) as AppCompatTextView
        val tvRate = constraintLayout.getChildAt(2) as AppCompatTextView
        val text = constraintLayout.getChildAt(3) as AppCompatTextView

        if (!obj.media.isNullOrEmpty()) {
            WidgetUtils.loadImageUrlRounded(img, obj.media?.get(0)?.content, SizeHelper.size4)
        }
        WidgetUtils.loadImageUrl(imgAvt, obj.owner?.avatar)

        if (!obj.name.isNullOrEmpty()) {
            tvName.text = obj.name
        }

        if (obj.rating != null) {
            tvRate.visibility = View.VISIBLE
            text.visibility = View.VISIBLE

            tvRate.text = obj.rating.toString()

            when {
                obj.rating >= 9 -> {
                    text rText R.string.fantastic
                }
                obj.rating >= 7 -> {
                    text rText R.string.great
                }
                obj.rating >= 6 -> {
                    text rText R.string.good
                }
                else -> {
                    text rText R.string.not_good
                }
            }
        } else {
            tvRate.visibility = View.INVISIBLE
            text.visibility = View.INVISIBLE
        }
    }
}