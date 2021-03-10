package vn.icheck.android.screen.user.viewimage.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import vn.icheck.android.network.models.ICThumbnail
import vn.icheck.android.ui.view.TouchImageView
import vn.icheck.android.util.kotlin.WidgetUtils

@SuppressLint("WrongConstant")
class ViewImageAdapter : PagerAdapter() {
    private val listData = mutableListOf<ICThumbnail>()

    fun setData(list: MutableList<ICThumbnail>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun getImageFromPosition(position: Int): String? {
        return listData[position].original
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = obj == view

    override fun getCount(): Int = listData.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = TouchImageView(container.context)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.layoutParams = layoutParams

        WidgetUtils.loadImageFitCenterUrl(imageView, listData[position].original)

        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }
}