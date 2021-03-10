package vn.icheck.android.component.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import retrofit2.http.POST
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.util.DimensionUtil

class IndicatorLineHorizontal : LinearLayout {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var current = 0

    override fun onFinishInflate() {
        super.onFinishInflate()
        removeAllViews()
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, SizeHelper.size4)
        orientation = HORIZONTAL
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun bind(count: Int) {
        removeAllViews()
        current = 0
        val lf = LayoutInflater.from(context)
        for (i in 0 until count) {
            val v = lf.inflate(R.layout.indicator_not_select, this, false)
            val lm = v.layoutParams as LayoutParams
            lm.weight = DimensionUtil.convertDpToPixel(1f, context)
            lm.marginStart = DimensionUtil.convertDpToPixel(5f, context).toInt()
            if (i == count - 1) {
                lm.marginEnd = DimensionUtil.convertDpToPixel(5f, context).toInt()
            }
            if (i == 0) {
                v.setBackgroundResource(R.drawable.bg_indicator_selected)
            }
            v.layoutParams = lm
            addView(v)
        }
    }

    fun setupViewPager(viepager: ViewPager) {
        removeAllViews()
        current = 0
        viepager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position != current) {
                    getChildAt(current).setBackgroundResource(R.drawable.bg_indicator_not_select)
                    getChildAt(position).setBackgroundResource(R.drawable.bg_indicator_selected)
                    current = position
                }
            }
        })
    }
}