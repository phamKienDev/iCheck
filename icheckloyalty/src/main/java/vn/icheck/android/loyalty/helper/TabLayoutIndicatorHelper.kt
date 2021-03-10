package vn.icheck.android.loyalty.helper

import android.view.View
import android.widget.FrameLayout
import com.google.android.material.tabs.TabLayout

class TabLayoutIndicatorHelper {
    private var indicatorWidth = 0

    fun calculateIndicator(indicator: View, tabLayout: TabLayout) {
        indicatorWidth = tabLayout.width / tabLayout.tabCount

        //Assign new width
        val indicatorParams = indicator.layoutParams as FrameLayout.LayoutParams
        indicatorParams.width = indicatorWidth
        indicator.layoutParams = indicatorParams
    }

    fun slideIndicator(indicator: View, position: Int, positionOffset: Float) {
        val params = indicator.layoutParams as FrameLayout.LayoutParams

        //Multiply positionOffset with indicatorWidth to get translation
        val translationOffset = (positionOffset + position) * indicatorWidth
        params.leftMargin = translationOffset.toInt()
        indicator.layoutParams = params
    }
}