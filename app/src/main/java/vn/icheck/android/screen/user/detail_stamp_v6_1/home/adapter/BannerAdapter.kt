package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.item_image_banner_stamp.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICThumbnail
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.viewimage.ViewImageActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class BannerAdapter : PagerAdapter() {
    private val listData = mutableListOf<ICMedia>()

    fun setListData(list: List<ICMedia>) {
        listData.clear()
        listData.addAll(list)
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

        if (listData[position].type == Constant.VIDEO) {
            view.imgPlayVideo.visibility = View.VISIBLE
        } else {
            view.imgPlayVideo.visibility = View.GONE
        }

        WidgetUtils.loadImageFitCenterUrl(view.imgBanner, ImageHelper.getImageFromUrl(listData[position].url, ImageHelper.thumbLargeSize))

        view.setOnClickListener {
            if (listData[position].type == Constant.VIDEO) {
                listData[position].url?.let { url ->
                    ICheckApplication.currentActivity()?.let { activity ->
                        ActivityUtils.startActivity(activity, Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }
            } else {
                val listImage = mutableListOf<ICThumbnail>()

                for (url in listData) {
                    val thumbnail = ICThumbnail()
                    val mUrl = ImageHelper.getImageFromUrl(url.url, ImageHelper.thumbLargeSize)
                    thumbnail.medium = mUrl
                    thumbnail.original = mUrl
                    listImage.add(thumbnail)
                }

                ICheckApplication.currentActivity()?.let { activity ->
                    val intent = Intent(activity, ViewImageActivity::class.java)
                    intent.putExtra(vn.icheck.android.constant.Constant.DATA_1, JsonHelper.toJson(listImage))
                    intent.putExtra(vn.icheck.android.constant.Constant.DATA_2, position)
                    ActivityUtils.startActivity(activity, intent)
                }
            }
        }

        parent.addView(view)
        return view
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