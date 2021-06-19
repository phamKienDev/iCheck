package vn.icheck.android.component.product_review.count_review

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R

import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.screen.user.list_product_review.ListProductReviewActivity

class CountReviewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(creatView(parent.context)) {

    fun bind(obj: CountReviewModel, show: Boolean) {
        //show = true: hiện textview xem tất cả
        (itemView as ViewGroup).run {
            (getChildAt(0) as AppCompatTextView).run {
                text = String.format(itemView.context.getString(R.string.danh_gia_san_pham_x), obj.count)
            }

            (getChildAt(1) as AppCompatTextView).run {
                setOnClickListener {
                    ICheckApplication.currentActivity()?.let {
                        ListProductReviewActivity.startActivity(obj.productId, it)
                    }
                }
                visibility = if (show) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    companion object {
        fun creatView(context: Context): LinearLayout {
            val layoutParent = LinearLayout(context)
            layoutParent.layoutParams = ViewHelper.createLayoutParams().also {
                it.topMargin = SizeHelper.size10
            }
            layoutParent.orientation = LinearLayout.HORIZONTAL
            layoutParent.gravity = Gravity.CENTER_VERTICAL
            layoutParent.setBackgroundColor(Constant.getAppBackgroundGrayColor(context))

            val secondaryColor = Constant.getSecondaryColor(context)

            layoutParent.addView(AppCompatTextView(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).also {
                    it.leftMargin = SizeHelper.size12
                }
                it.setTextColor(secondaryColor)
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                it.includeFontPadding = false
            })

            layoutParent.addView(AppCompatTextView(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT).also {
                    it.rightMargin = SizeHelper.size12
                }
                it.setTextColor(secondaryColor)
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.gravity = Gravity.CENTER_VERTICAL
                it.setBackgroundColor(Color.TRANSPARENT)
                it.textSize = 14f
                it.includeFontPadding = false
                it.text = context.getString(R.string.xem_tat_ca)
            })

            return layoutParent
        }
    }
}