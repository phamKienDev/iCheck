package vn.icheck.android.screen.user.campaign.calback

import vn.icheck.android.network.models.ICAds

interface IBannerSurveyListener {

    fun onBannerSurveyClicked(ads: ICAds)
}