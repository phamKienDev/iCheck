package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICDetail_Campaign

interface IDetailCampaignView : BaseActivityView {
    fun onGetDataError(errorMessage: String)
    fun onGetDataSucces(data: ICDetail_Campaign)
}