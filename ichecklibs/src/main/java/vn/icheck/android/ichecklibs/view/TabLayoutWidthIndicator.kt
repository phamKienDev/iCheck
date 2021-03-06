package vn.icheck.android.ichecklibs.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import com.google.android.material.tabs.TabLayout
import java.lang.reflect.Field

internal class TabLayoutWidthIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TabLayout(context, attrs, defStyleAttr) {

    private fun init(attrs: AttributeSet?) {
        isTabIndicatorFullWidth = false
        setIndicatorWidth(70)
    }

    private inner class DefPreDrawListener : ViewTreeObserver.OnPreDrawListener {
        private var tabStrip: LinearLayout? = null
        private var tabWidth = 0
        private var fieldLeft: Field? = null
        private var fieldRight: Field? = null
        fun setTabStrip(tabStrip: LinearLayout, width: Int) {
            try {
                this.tabStrip = tabStrip
                tabWidth = width
                val cls: Class<*> = tabStrip.javaClass
                fieldLeft = cls.getDeclaredField("indicatorLeft")
                fieldLeft!!.isAccessible = true
                fieldRight = cls.getDeclaredField("indicatorRight")
                fieldRight!!.isAccessible = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onPreDraw(): Boolean {
            try {
                if (tabWidth > 0) {
                    var left = fieldLeft!!.getInt(tabStrip)
                    var right = fieldRight!!.getInt(tabStrip)
                    val diff = right - left - tabWidth
                    left += diff / 2
                    right -= diff / 2
                    fieldLeft!!.setInt(tabStrip, left)
                    fieldRight!!.setInt(tabStrip, right)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
    }

    private val defPreDrawListener = DefPreDrawListener()
    fun setIndicatorWidth(widthDp: Int) {
        val tabLayout: Class<*> = TabLayout::class.java
        var tabStrip: Field? = null
        try {
            tabStrip = tabLayout.getDeclaredField("slidingTabIndicator")
            tabStrip.isAccessible = true
            val tabIndicator = tabStrip[this] as LinearLayout
            val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp.toFloat(), Resources.getSystem().displayMetrics).toInt()
            //avoid add preDrawListener multi times
            tabIndicator.viewTreeObserver.removeOnPreDrawListener(defPreDrawListener)
            tabIndicator.viewTreeObserver.addOnPreDrawListener(defPreDrawListener)
            defPreDrawListener.setTabStrip(tabIndicator, width)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        init(attrs)
    }
}