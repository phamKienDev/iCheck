package vn.icheck.android.screen.user.detail_stamp_v6.home.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import vn.icheck.android.R
import vn.icheck.android.network.models.ICThumbnail
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectImageProductV6
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.detail_stamp_v6.home.view.IDetailStampV6View
import vn.icheck.android.util.kotlin.WidgetUtils

class BannerV6Adapter(val context: Context?, val viewCallback: IDetailStampV6View) : PagerAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val listData = mutableListOf<ICObjectImageProductV6?>()

    fun setListImageData(list: MutableList<ICObjectImageProductV6>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return listData.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
//        val imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false)!!

        val imageView = AppCompatImageView(view.context)
        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        WidgetUtils.loadImageFitCenterUrl(imageView, getUrl(listData[position]?.url))
        view.addView(imageView, 0)

        imageView.setOnClickListener {
            val listImage = mutableListOf<ICThumbnail>()

            for (listUrl in listData) {
                val thumbnail = ICThumbnail()
                val mUrl = getUrl(listUrl?.url)
                thumbnail.medium = mUrl
                thumbnail.original = mUrl
                listImage.add(thumbnail)
            }
            viewCallback.itemPagerClick(JsonHelper.toJson(listImage), position)
        }

        return imageView
    }

    private fun getUrl(url: String?): String? {
        return if (url?.startsWith("http") == true) {
            url
        } else {
            "http://icheckcdn.net/images/480x480/$url.jpg"
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {

    }

    override fun saveState(): Parcelable? {
        return null
    }
}