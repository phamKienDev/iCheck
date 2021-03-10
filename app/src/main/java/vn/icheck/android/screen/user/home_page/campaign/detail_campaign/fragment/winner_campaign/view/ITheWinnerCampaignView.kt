package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.network.models.ICCampaign_User_Reward

interface ITheWinnerCampaignView : BaseFragmentView {
    fun onRefresh()
    fun onGetDataError(errorMessage: String)
    fun onSetListDataUserRewardCampaignSuccess(list: MutableList<ICCampaign_User_Reward>, isLoadMore: Boolean, count: Int)
    fun getDataIntentSuccess(id: String)
    fun onLoadMore()
    fun onClickItem(item: ICCampaign_User_Reward)
}