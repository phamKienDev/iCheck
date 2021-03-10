package vn.icheck.android.util

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.dialog.ads.BannerDialog
import vn.icheck.android.screen.dialog.ads.BannerSurveyDialog
import vn.icheck.android.screen.dialog.ads.DirectSurveyDialog
import vn.icheck.android.screen.dialog.ads.HtmlDialog
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.bannersurvey.BannerSurveyActivity
import vn.icheck.android.util.kotlin.ActivityUtils

object AdsUtils {

    fun showAdsPopup(fragmentActivity: FragmentActivity?, ads: ICAds) {
        when (ads.type) {
            Constant.DIRECT_SURVEY -> {
                fragmentActivity?.let {
                    DirectSurveyDialog(it, ads).show()
                }
            }
            Constant.BANNER_SURVEY -> {
                fragmentActivity?.let {
                    object : BannerSurveyDialog(it, ads) {
                        override fun onBannerClicked(ads: ICAds) {
                            ActivityUtils.startActivity<BannerSurveyActivity>(it, Constant.DATA_1, JsonHelper.toJson(ads))
                        }
                    }.show()
                }
            }
            Constant.BANNER -> {
                ads.banner_url?.let {
                    fragmentActivity?.let {
                        object : BannerDialog(it, ads) {
                            override fun onClicked(obj: ICAds) {
                                bannerClicked(it, ads)
                            }
                        }.show()
                    }
                }
            }
            Constant.URL -> {
                ads.destination_url?.let { url ->
                    fragmentActivity?.let {
                        HtmlDialog(it, url, true).show()
                    }
                }
            }
            Constant.HTML -> {
                ads.html?.let { html ->
                    fragmentActivity?.let {
                        HtmlDialog(it, html, false).show()
                    }
                }
            }
        }
    }

    fun bannerClicked(fragmentActivity: Activity?, ads: ICAds?) {
        if (fragmentActivity != null && ads?.destination_url != null) {
            if (ads.destination_url!!.contains(Constant.COLLECTION)) {
                FirebaseDynamicLinksActivity.startDestinationUrl(fragmentActivity, (ads.destination_url!! + "&name=${ads.collection?.name}"))
            } else {
                FirebaseDynamicLinksActivity.startDestinationUrl(fragmentActivity, ads.destination_url!!)
            }
        }
    }

    fun bannerClicked(fragmentActivity: Activity?, destinationUrl: String?, name: String? = null) {
        if (fragmentActivity != null && destinationUrl != null) {
            if (!name.isNullOrEmpty()) {
                FirebaseDynamicLinksActivity.startDestinationUrl(fragmentActivity, (destinationUrl + "&name=${name}"))
            } else {
                FirebaseDynamicLinksActivity.startDestinationUrl(fragmentActivity, destinationUrl)
            }
        }
    }

    fun bannerSurveyClicked(fragmentActivity: FragmentActivity, requestCode: Int, ads: ICAds) {
        ActivityUtils.startActivityForResult<BannerSurveyActivity>(fragmentActivity, Constant.DATA_1, JsonHelper.toJson(ads), requestCode)
    }

    fun bannerSurveyClicked(fragment: Fragment, requestCode: Int, ads: ICAds) {
        ActivityUtils.startActivityForResult<BannerSurveyActivity>(fragment, Constant.DATA_1, JsonHelper.toJson(ads), requestCode)
    }

    fun bannerSurveyClicked(fragment: Fragment, requestCode: Int, id: Long) {
        ActivityUtils.startActivityForResult<BannerSurveyActivity, Long>(fragment, Constant.DATA_2, id, requestCode)
    }
}