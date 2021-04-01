package vn.icheck.android.component.banner

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.item_ads_banner.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICAdsData
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.campaign.calback.IBannerV2Listener
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.AdsUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.net.URL

class SlideBannerV2Adapter(val listData: MutableList<ICAdsData>, private val topBannerListener: IBannerV2Listener) : PagerAdapter() {

    val getListData: MutableList<ICAdsData>
        get() {
            return listData
        }

    override fun isViewFromObject(view: View, obj: Any): Boolean = obj == view

    override fun getCount(): Int = listData.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context).inflate(R.layout.item_ads_banner, container, false)
        val banner = listData[position]

        if (!banner.media.isNullOrEmpty()) {
            for (i in banner.media ?: mutableListOf()) {
                if (i.type == "video") {
                    itemView.imgPlay.visibility = View.VISIBLE
                } else {
                    itemView.imgPlay.visibility = View.GONE
                }
            }
            WidgetUtils.loadImageUrlRoundedNotTransform(itemView.imgImage, banner.media!![0].content, R.drawable.img_default_loading_icheck,SizeHelper.size4)
        }

        itemView.setOnClickListener {
            if (!banner.media.isNullOrEmpty()){
                if (banner.media!![0].type == "video") {
                    if (!banner.media!![0].content.isNullOrEmpty()) {
                        ICheckApplication.currentActivity()?.let {
                            DetailMediaActivity.start(it, banner.media!![0].content!!, Constant.VIDEO)
                        }
                    }
                } else {
                    if (!banner.targetId.isNullOrEmpty()) {
                       ICheckApplication.currentActivity()?.let {
                           FirebaseDynamicLinksActivity.startTarget(it,banner.targetType,banner.targetId)
                       }

//                        val deepLink = Uri.parse(banner.targetType)
//                        val targetType = try {
//                            if (deepLink?.scheme == "icheck") {
//                                deepLink.host ?: ""
//                            } else {
//                                URL(deepLink.toString()).path.removePrefix("/")
//                            }
//                        } catch (e: Exception) {
//                            ""
//                        }
//
//                        if (targetType == "survey") {
//                            try {
//                                val id = deepLink.getQueryParameter("id")
//                                topBannerListener.onBannerSurveyClicked(id!!.toLong())
//                            } catch (e: Exception) {
//                            }
//                        } else {
//                            ICheckApplication.currentActivity()?.let { activity ->
//                                AdsUtils.bannerClicked(activity, listData[position].targetType)
//                            }
//                        }
                    }
                }
            }
        }

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }
}