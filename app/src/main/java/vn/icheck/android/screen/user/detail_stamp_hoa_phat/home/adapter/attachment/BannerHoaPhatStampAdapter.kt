package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.attachment

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.item_image_banner_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.regex.Pattern

class BannerHoaPhatStampAdapter(val context: Context?,val viewCallback: SlideHeaderStampHoaPhatListener) : PagerAdapter() {
    private val listData = arrayListOf<ICBarcodeProductV1.Attachments>()

    private var url = ""

    fun setListImageData(list: List<ICBarcodeProductV1.Attachments>) {
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

        val urlImage = if (listData[position].type == "video"){
            getYoutubeVideoId(listData[position].thumbnails.original)
        } else {
            listData[position].thumbnails.original
        }

        if (listData[position].type == "video") {
            view.imgPlayVideo.visibility = View.VISIBLE
            url = "https://img.youtube.com/vi/$urlImage/hqdefault.jpg"
        } else {
            view.imgPlayVideo.visibility = View.GONE
            if (urlImage != null) {
                url = urlImage
            }
        }


        WidgetUtils.loadImageFitCenterUrl(view.imgBanner, url,R.drawable.update_product_holder)

        view.setOnClickListener {
            if (listData[position].type == "video"){
                viewCallback.itemPagerClickToVideo(listData[position].thumbnails.original)
            } else {
                viewCallback.itemPagerClickToImage(listData[position].thumbnails.original, position)
            }
//            if (urlImage == listData[position]) {
//                viewCallback.itemPagerClickToVideo(urlVideo)
//            } else {
//                val listImage = mutableListOf<ICThumbnail>()
//
//                for (url in listData) {
//                    val thumbnail = ICThumbnail()
//                    val mUrl = getUrl(url)
//                    thumbnail.medium = mUrl
//                    thumbnail.original = mUrl
//                    listImage.add(thumbnail)
//                }

//                viewCallback.itemPagerClick(listData, position)
//            }
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

    private fun getYoutubeVideoId(youtubeUrl: String?): String? {
        var videoId: String? = ""
        if (youtubeUrl != null && youtubeUrl.trim { it <= ' ' }.isNotEmpty() && youtubeUrl.startsWith("http")) {
            val expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(youtubeUrl)
            if (matcher.matches()) {
                val groupIndex1: String = matcher.group(7)
                if (groupIndex1 != null && groupIndex1.length == 11) videoId = groupIndex1
            }
        }
        return videoId
    }
}