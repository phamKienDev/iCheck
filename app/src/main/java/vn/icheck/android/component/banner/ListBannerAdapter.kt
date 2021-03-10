package vn.icheck.android.component.banner

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.screen.user.campaign.calback.IBannerListener
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.util.kotlin.WidgetUtils

class ListBannerAdapter(val listData: MutableList<ICAds>, private val topBannerListener: IBannerListener) : PagerAdapter() {

    val getListData: MutableList<ICAds>
        get() {
            return listData
        }

    fun updateBannerSurvey(obj: ICAds) {
        for (i in listData.indices) {
            if (listData[i].id == obj.id) {
                listData[i] = obj
                notifyDataSetChanged()
                return
            }
        }
    }

    fun removeBannerSurvey(surveyID: Long) {
        for (i in listData.indices) {
            if (listData[i].id == surveyID) {
                listData.removeAt(i)
                notifyDataSetChanged()
                return
            }
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = obj == view

    override fun getCount(): Int = listData.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = ViewHelper.createBannerHolder(container.context, 0, 0)
        val banner = listData[position]

        WidgetUtils.loadImageUrlRounded(itemView.getChildAt(0) as AppCompatImageView, ImageHelper.getImageUrl(banner.banner_id, banner.banner_thumbnails?.original, ImageHelper.originalSize), R.drawable.ic_default_horizontal, SizeHelper.size10)

        itemView.setOnClickListener {
            if (banner.type == Constant.BANNER_SURVEY) {
                topBannerListener.onBannerSurveyClicked(banner)
            } else {
//                topBannerListener.onBannerClicked(banner)
            }
        }

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }
}