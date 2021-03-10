package vn.icheck.android.screen.user.campaign.calback

import vn.icheck.android.network.models.ICAds

interface IBannerListener {

    fun onBannerSurveyClicked(ads: ICAds)
}