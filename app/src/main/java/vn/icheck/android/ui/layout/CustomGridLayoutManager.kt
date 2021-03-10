package vn.icheck.android.ui.layout

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by lecon on 4/1/2018
 */
internal class CustomGridLayoutManager(
        context: Context?,
        spanCount: Int,
        orientation: Int = RecyclerView.VERTICAL,
        reverseLayout: Boolean = false,
        private val isScrollVertical: Boolean = false,
        private val iisScrollHorizontal: Boolean = false) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {

    override fun canScrollVertically(): Boolean {
        return isScrollVertical && super.canScrollVertically()
    }

    override fun canScrollHorizontally(): Boolean {
        return iisScrollHorizontal && super.canScrollHorizontally()
    }
}