package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign.presenter

import android.os.Bundle
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICCampaign_User_Reward
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign.view.ITheWinnerCampaignView

class TheWinnerCampaignPresenter (val view : ITheWinnerCampaignView): BaseFragmentPresenter(view) {

    private var interactor = ListCampaignInteractor()
    private var page = 0

    fun getDataObject(arguments: Bundle?) {
        val obj = try {
            arguments?.getSerializable(Constant.DATA_1) as ICDetail_Campaign
        } catch (e: Exception) {
            null
        }
        obj?.id?.let { view.getDataIntentSuccess(it) }
    }

    fun getUserRewardCampaign(id: String?,isLoadMore: Boolean = false) {

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (id.isNullOrEmpty()){
            view.onGetDataError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        if (!isLoadMore)
            page = 0

        interactor.getListUserRewardCampaign(id,page,object : ICApiListener<ICListResponse<ICCampaign_User_Reward>> {
            override fun onSuccess(obj: ICListResponse<ICCampaign_User_Reward>) {
                if (!obj.rows.isNullOrEmpty()) {
                    page += APIConstants.LIMIT
                    view.onSetListDataUserRewardCampaignSuccess(obj.rows, isLoadMore, obj.count)
                }else{
                    view.onSetListDataUserRewardCampaignSuccess(mutableListOf(), isLoadMore, obj.count)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetDataError(getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai))
            }
        })
    }

}