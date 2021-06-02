package vn.icheck.android.screen.user.component.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.Constant


class TabSegment : FrameLayout {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("InflateParams", "NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onFinishInflate() {
        super.onFinishInflate()
        removeAllViews()

        val view = View(context)
        view.layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT)
        view.id = R.id.indicator
        view.setBackgroundResource(R.drawable.bg_blue_corners_40)
        addView(view)

        val tabLayout = TabLayout(context)
        tabLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        tabLayout.setBackgroundResource(android.R.color.transparent)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.tabMode = TabLayout.MODE_FIXED
        tabLayout.setSelectedTabIndicator(null)
        tabLayout.setSelectedTabIndicatorHeight(0)
        tabLayout.tabRippleColor = null
        tabLayout.setTabTextColors(vn.icheck.android.ichecklibs.Constant.getSecondaryColor(context), Constant.getAppBackgroundWhiteColor(context))

        addView(tabLayout)
    }

    fun setupWithViewPager(viepager: ViewPager) {
        val indicator = getChildAt(0) as View
        val tabLayout = getChildAt(1) as TabLayout

        tabLayout.setupWithViewPager(viepager)

        tabLayout.post {
            for (i in 0 until tabLayout.tabCount) {
                val tab = tabLayout.getTabAt(i)
                if (tab != null) {
                    val tabTextView = TextView(context)
                    tab.customView = tabTextView
                    tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    tabTextView.text = tab.text
                    tabTextView.textSize = 14f
                    tabTextView.isAllCaps = false
                    val type = ResourcesCompat.getFont(context,R.font.barlow_medium)
                    tabTextView.typeface = type

                    if (i == 0) {
                        tabTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                    } else {
                        tabTextView.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondaryColor(context))
                    }
                }
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {

                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                    val vg = tabLayout.getChildAt(0) as ViewGroup
                    val vgTab = vg.getChildAt(p0!!.position) as ViewGroup
                    val tabChildsCount = vgTab.childCount
                    for (i in 0 until tabChildsCount) {
                        val tabViewChild = vgTab.getChildAt(i)
                        if (tabViewChild is TextView) {
                            tabViewChild.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondaryColor(context))
                        }
                    }
                }

                override fun onTabSelected(p0: TabLayout.Tab?) {
                    val vg = tabLayout.getChildAt(0) as ViewGroup
                    val vgTab = vg.getChildAt(p0!!.position) as ViewGroup
                    val tabChildsCount = vgTab.childCount
                    for (i in 0 until tabChildsCount) {
                        val tabViewChild = vgTab.getChildAt(i)
                        if (tabViewChild is TextView) {
                            tabViewChild.setTextColor(ContextCompat.getColor(context, R.color.white))
                        }
                    }
                }
            })

            val indicatorWidth = tabLayout.width / tabLayout.tabCount
            val indicatorParams = indicator.layoutParams as LayoutParams
            indicatorParams.width = indicatorWidth
            indicator.layoutParams = indicatorParams

            viepager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    indicator.layoutParams = (indicator.layoutParams as LayoutParams).also {
                        it.leftMargin = ((positionOffset + position) * indicatorWidth).toInt()
                    }
                }

                override fun onPageSelected(position: Int) {}
            })
        }
    }
}