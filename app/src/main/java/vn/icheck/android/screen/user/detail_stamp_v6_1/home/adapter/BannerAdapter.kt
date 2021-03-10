package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.item_image_banner_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICThumbnail
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.view.IDetailStampView
import vn.icheck.android.util.kotlin.WidgetUtils

class BannerAdapter(val context: Context?, val viewCallback: IDetailStampView) : PagerAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val listData = mutableListOf<String>()

    private var urlVideo: String? = null
    private var urlImage: String? = null

    fun setListImageData(list: MutableList<String>, urlVideo: String?, urlImage: String?) {
        listData.clear()
        listData.addAll(list)
        this.urlVideo = urlVideo
        this.urlImage = urlImage
        notifyDataSetChanged()
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int {
        return listData.size
    }

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_banner_stamp, parent, false)

        if (urlImage == listData[position]) {
            view.imgPlayVideo.visibility = View.VISIBLE
        } else {
            view.imgPlayVideo.visibility = View.GONE
        }

        WidgetUtils.loadImageFitCenterUrl(view.imgBanner, getUrl(listData[position]))

        view.setOnClickListener {
            if (urlImage == listData[position]) {
                viewCallback.itemPagerClickToVideo(urlVideo)
            } else {
                val listImage = mutableListOf<ICThumbnail>()

                for (url in listData) {
                    val thumbnail = ICThumbnail()
                    val mUrl = getUrl(url)
                    thumbnail.medium = mUrl
                    thumbnail.original = mUrl
                    listImage.add(thumbnail)
                }

                viewCallback.itemPagerClick(JsonHelper.toJson(listImage), position)
            }
        }

        parent.addView(view)
        return view
    }

    private fun getUrl(url: String?): String? {
        return if (url?.startsWith("http") == true) {
            url
        } else {
            "http://icheckcdn.net/images/480x480/$url.jpg"
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {

    }

    override fun saveState(): Parcelable? {
        return null
    }
}