package vn.icheck.android.screen.user.page_details.fragment.page.widget.brand

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.view.second_text.TextSecondBarlowMedium
import vn.icheck.android.network.models.ICPageTrend
import vn.icheck.android.screen.user.brand.BrandPageActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class WidgetBrandPageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(createView(parent.context)) {

    fun bind(obj: MutableList<ICPageTrend>, type: Int) {
        val params = itemView as LinearLayout

        (params.getChildAt(0) as LinearLayout).run {
            setOnClickListener {
                ICheckApplication.currentActivity()?.let { act ->
                    ActivityUtils.startActivity<BrandPageActivity, Long>(act, Constant.DATA_1, 1)
                }
            }
            (getChildAt(0) as AppCompatTextView).run {
                when (type) {
                    Constant.PAGE_BRAND_TYPE -> {
                        text = context.getString(R.string.cac_nhan_hang)
                    }
                    Constant.PAGE_EXPERT_TYPE -> {
                        text = context.getString(R.string.dai_su_thuong_hieu)
                    }
                }
            }
            (getChildAt(1) as AppCompatTextView).run {
                text = "(${obj.size})"
            }
            (getChildAt(2) as AppCompatImageView).run {
                setOnClickListener {
                    ICheckApplication.currentActivity()?.let { act ->
                        ActivityUtils.startActivity<BrandPageActivity, Long>(act, Constant.DATA_1, 1)
                    }
                }
            }
        }

        (params.getChildAt(1) as RecyclerView).run {
            layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = WidgetBrandPageAdapter(obj)
        }
    }

    companion object {
        fun createView(context: Context): View {
            return LinearLayout(context).also { layoutParams ->
                layoutParams.layoutParams = ViewHelper.createLayoutParams()
                layoutParams.setPadding(0, SizeHelper.size16, 0, 0)
                layoutParams.orientation = LinearLayout.VERTICAL

                layoutParams.addView(LinearLayout(context).also { linear ->
                    linear.layoutParams = ViewHelper.createLayoutParams().also { params ->
                        params.setMargins(SizeHelper.size12, 0, SizeHelper.size12, 0)
                    }
                    linear.orientation = LinearLayout.HORIZONTAL
                    linear.gravity = Gravity.CENTER

                    linear.addView(AppCompatTextView(context).also { text ->
                        text.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        text.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                        text.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondaryColor(context))
                        text.includeFontPadding = false
                        text.setText(R.string.cac_nhan_hang)
                        text.setPadding(0, 0, 0, 0)
                    })

                    linear.addView(TextSecondBarlowMedium(context).also { text ->
                        text.layoutParams = ViewHelper.createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f, SizeHelper.size3, 0, 0, 0)
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        text.gravity = Gravity.CENTER_VERTICAL
                        text.includeFontPadding = false
                        text.setPadding(0, 0, SizeHelper.size12, 0)
                    })

                    linear.addView(AppCompatImageView(context).also { img ->
                        img.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        img.setImageResource(R.drawable.ic_more_gray_14)
                    })
                })

                layoutParams.addView(RecyclerView(context).also { recyclerView ->
                    recyclerView.layoutParams = ViewHelper.createLayoutParams().also { params ->
                        params.setMargins(0, SizeHelper.size12, 0, SizeHelper.size10)
                    }
                    recyclerView.clipToPadding = false
                })
            }
        }
    }
}