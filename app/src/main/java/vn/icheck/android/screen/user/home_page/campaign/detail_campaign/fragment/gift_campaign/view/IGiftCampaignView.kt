package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICCampaign_Reward

interface IGiftCampaignView : BaseFragmentView {
    fun onGetDataError(errorMessage: String)
    fun onSetListDataCampaignRewardSuccess(data: MutableList<ICCampaign_Reward>, loadMore: Boolean, id: String)
    fun onLoadMore()
}