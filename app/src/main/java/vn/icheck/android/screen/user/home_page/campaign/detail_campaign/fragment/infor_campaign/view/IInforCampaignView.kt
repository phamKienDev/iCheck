package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICDetail_Campaign

interface IInforCampaignView : BaseFragmentView {
    fun getDataSuccess(obj: ICDetail_Campaign)
    fun onGetDataError(message: String)
    fun showLoading()
    fun closeLoading()
    fun onJoinCampaignSuccess(message: String)
    fun onCickSponsor(item: ICDetail_Campaign.ListSponsors)
}