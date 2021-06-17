package vn.icheck.android.component.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.util.DimensionUtil

class IndicatorLineHorizontal : LinearLayout {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var current = 0
    private var mMarginStart = vn.icheck.android.ichecklibs.SizeHelper.size5
    private var mMarginEnd = vn.icheck.android.ichecklibs.SizeHelper.size5

    fun setItemPadding(size: Int) {
        mMarginStart = size
        mMarginEnd = size
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        removeAllViews()
        orientation = HORIZONTAL
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun bind(count: Int) {
        removeAllViews()
        current = 0

        for (i in 0 until count) {
            addView(View(context).apply {
                layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1f).apply {
                    marginStart = mMarginStart
                    if (i == count - 1) {
                        marginEnd = mMarginEnd
                    }
                }

                background = if (i == current) {
                    ViewHelper.bgWhiteCorners14(context)
                } else {
                    ContextCompat.getDrawable(context, R.drawable.bg_indicator_not_select)
                }
            })
        }
    }

    fun setupViewPager(viewPager: ViewPager) {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position != current) {
                    getChildAt(current)?.background = ContextCompat.getDrawable(context, R.drawable.bg_indicator_not_select)
                    getChildAt(position)?.background = ViewHelper.bgWhiteCorners14(context)
                    current = position
                }
            }
        })
    }

    fun setupViewPager(viewPager: ViewPager, size: Int) {
        bind(size)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position != current) {
                    getChildAt(current)?.background = ContextCompat.getDrawable(context, R.drawable.bg_indicator_not_select)
                    getChildAt(position)?.background = ViewHelper.bgWhiteCorners14(context)
                    current = position
                }
            }
        })
    }
}