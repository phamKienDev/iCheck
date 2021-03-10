package vn.icheck.android.component.banner

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.screen.user.campaign.calback.IBannerListener
import vn.icheck.android.util.AdsUtils

class BannerHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ViewHelper.createBannerHolder(parent.context)) {

    fun bind(obj: ICAds, listener: IBannerListener) {
        (itemView as ConstraintLayout).run {
            val imgBanner = getChildAt(0) as AppCompatImageView

            ViewHelper.setupBanner(this, imgBanner, obj.banner_size, ImageHelper.getImageUrl(obj.banner_id, obj.banner_thumbnails?.original, ImageHelper.originalSize))

            setOnClickListener {
                if (obj.type == Constant.BANNER_SURVEY) {
                    listener.onBannerSurveyClicked(obj)
                } else {
                    ICheckApplication.currentActivity()?.let { activity ->
                        AdsUtils.bannerClicked(activity, obj)
                    }
                }
            }
        }
    }
}