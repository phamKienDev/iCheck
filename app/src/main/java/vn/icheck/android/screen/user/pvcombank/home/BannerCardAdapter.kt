package vn.icheck.android.screen.user.pvcombank.detail_card

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.item_ads_banner.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICAdsData
import vn.icheck.android.screen.user.campaign.calback.IBannerV2Listener
import vn.icheck.android.util.AdsUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.net.URL

class BannerCardAdapter(val listData: MutableList<String>) : PagerAdapter() {

    val getListData: MutableList<String>
        get() {
            return listData
        }

    override fun isViewFromObject(view: View, obj: Any): Boolean = obj == view

    override fun getCount(): Int = listData.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context).inflate(R.layout.item_ads_banner, container, false)
        val banner = listData[position]

        WidgetUtils.loadImageUrlRounded(itemView.imgImage, banner, SizeHelper.size4)

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }
}