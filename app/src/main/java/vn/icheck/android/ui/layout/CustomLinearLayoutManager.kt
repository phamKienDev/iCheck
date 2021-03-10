package vn.icheck.android.ui.layout

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by lecon on 4/1/2018
 */
class CustomLinearLayoutManager(context: Context, orientation: Int = RecyclerView.VERTICAL, reverseLayout: Boolean = false, var isScrollEnabled: Boolean = false) : LinearLayoutManager(context, orientation, reverseLayout) {

    fun setScroll(isEnable: Boolean) {
        isScrollEnabled = isEnable
    }

    override fun canScrollVertically(): Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }

    override fun canScrollHorizontally(): Boolean {
        return isScrollEnabled && super.canScrollHorizontally()
    }
}