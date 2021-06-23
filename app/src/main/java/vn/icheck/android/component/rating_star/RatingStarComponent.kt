package vn.icheck.android.component.rating_star

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper

class RatingStarComponent : LinearLayout {

    constructor(context: Context) : super(context) {
        setUpView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setUpView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUpView()
    }

    private fun setUpView() {
        layoutParams = ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL

        addView(AppCompatTextView(context).also { tvPoint ->
            tvPoint.layoutParams = ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    .also {
                        it.setMargins(0, 0, SizeHelper.size6, 0)
                    }
            tvPoint.setPadding(SizeHelper.size6, SizeHelper.size1, SizeHelper.size6, SizeHelper.size1)
            tvPoint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            tvPoint.includeFontPadding = false
            tvPoint.lineHeight = SizeHelper.size16
            tvPoint.typeface = ViewHelper.createTypeface(context, R.font.barlow_semi_bold)

        })

        val ratingView = LayoutInflater.from(context).inflate(R.layout.item_product_rating_bar_new, this, false)
        addView(ratingView)

        addView(AppCompatImageView(context).also { image ->
            image.layoutParams = ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down_gray_24dp))
        })
    }

    fun setData(point: Float, rate: Float) {
        val pointDouble = point * 2
        (getChildAt(0) as AppCompatTextView).run {
            when {
                pointDouble < 6 -> {
                    text = context.getString(R.string.x_diem_danh_gia, pointDouble)
                    setTextColor(ContextCompat.getColor(context, R.color.light_purple))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.light_purple), SizeHelper.size14.toFloat())
                }
                pointDouble < 7 -> {
                    text = context.getString(R.string.x_hai_long, pointDouble)
                    setTextColor(ContextCompat.getColor(context, R.color.green_v2))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.green_v2), SizeHelper.size14.toFloat())
                }
                pointDouble < 8 -> {
                    text = context.getString(R.string.x_tot, pointDouble)
                    setTextColor(ContextCompat.getColor(context, R.color.orange_v2))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.orange_v2), SizeHelper.size14.toFloat())
                }
                pointDouble < 9 -> {
                    text = context.getString(R.string.x_tuyet_voi, pointDouble)
                    setTextColor(ContextCompat.getColor(context, R.color.red_v2))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.red_v2), SizeHelper.size14.toFloat())
                }
                else -> {
                    val primaryColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(context)
                    text = context.getString(R.string.x_tren_ca_tuyet_voi, pointDouble)
                    setTextColor(primaryColor)
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, primaryColor, SizeHelper.size14.toFloat())
                }
            }
        }

        (getChildAt(1) as AppCompatRatingBar).run {
            rating = rate
        }


        (getChildAt(2) as AppCompatImageView).run {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down_gray_24dp))
        }
    }
}