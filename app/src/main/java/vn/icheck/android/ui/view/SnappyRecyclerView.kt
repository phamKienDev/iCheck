package vn.icheck.android.ui.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class SnappyRecyclerView : RecyclerView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle)

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val linearLayoutManager = layoutManager as LinearLayoutManager?
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels

        // views on the screen
        val lastVisibleItemPosition = linearLayoutManager!!.findLastVisibleItemPosition()
        val lastView = linearLayoutManager.findViewByPosition(lastVisibleItemPosition)
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val firstView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition)

        // distance we need to scroll
        val leftMargin = (screenWidth - lastView!!.width) / 2
        val rightMargin = (screenWidth - firstView!!.width) / 2 + firstView.width
        val leftEdge = lastView.left
        val rightEdge = firstView.right
        val scrollDistanceLeft = leftEdge - leftMargin
        val scrollDistanceRight = rightMargin - rightEdge
        return if (Math.abs(velocityX) < 1000) {
            // The fling is slow -> stay at the current page if we are less than half through,
            // or go to the next page if more than half through
            if (leftEdge > screenWidth / 2) {
                // go to next page
                smoothScrollBy(-scrollDistanceRight, 0)
            } else if (rightEdge < screenWidth / 2) {
                // go to next page
                smoothScrollBy(scrollDistanceLeft, 0)
            } else {
                // stay at current page
                if (velocityX > 0) {
                    smoothScrollBy(-scrollDistanceRight, 0)
                } else {
                    smoothScrollBy(scrollDistanceLeft, 0)
                }
            }
            true
        } else {
            // The fling is fast -> go to next page
            if (velocityX > 0) {
                smoothScrollBy(scrollDistanceLeft, 0)
            } else {
                smoothScrollBy(-scrollDistanceRight, 0)
            }
            true
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        // If you tap on the phone while the RecyclerView is scrolling it will stop in the middle.
        // This code fixes this. This code is not strictly necessary but it improves the behaviour.
        if (state == SCROLL_STATE_IDLE) {
            val linearLayoutManager = layoutManager as LinearLayoutManager?
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels

            // views on the screen
            val lastVisibleItemPosition = linearLayoutManager!!.findLastVisibleItemPosition()
            val lastView = linearLayoutManager.findViewByPosition(lastVisibleItemPosition)
            val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
            val firstView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition)

            // distance we need to scroll
            val leftMargin = (screenWidth - lastView!!.width) / 2
            val rightMargin = (screenWidth - firstView!!.width) / 2 + firstView.width
            val leftEdge = lastView.left
            val rightEdge = firstView.right
            val scrollDistanceLeft = leftEdge - leftMargin
            val scrollDistanceRight = rightMargin - rightEdge
            if (leftEdge > screenWidth / 2) {
                smoothScrollBy(-scrollDistanceRight, 0)
            } else if (rightEdge < screenWidth / 2) {
                smoothScrollBy(scrollDistanceLeft, 0)
            }
        }
    }
}