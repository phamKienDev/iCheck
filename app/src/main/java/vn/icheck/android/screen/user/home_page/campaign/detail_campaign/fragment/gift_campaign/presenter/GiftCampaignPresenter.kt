package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign.presenter

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
import vn.icheck.android.network.models.ICCampaign_Reward
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign.view.IGiftCampaignView

class GiftCampaignPresenter(val view: IGiftCampaignView) : BaseFragmentPresenter(view) {

    private var interactor = ListCampaignInteractor()

    private var offset = 0

    fun getDataObject(arguments: Bundle?) {
        val obj = try {
            arguments?.getSerializable(Constant.DATA_1) as ICDetail_Campaign
        } catch (e: Exception) {
            null
        }
        getGiftCampaign(obj!!.id, false)
    }

    fun getGiftCampaign(id: String?, isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (id.isNullOrEmpty()) {
            view.onGetDataError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        if (!isLoadMore)
            offset = 0

        interactor.getRewardCampaign(id, offset, APIConstants.LIMIT, object : ICApiListener<ICListResponse<ICCampaign_Reward>> {
            override fun onSuccess(obj: ICListResponse<ICCampaign_Reward>) {
                if (obj.rows != null && !obj.rows.isNullOrEmpty()) {
                    offset += APIConstants.LIMIT
                    view.onSetListDataCampaignRewardSuccess(obj.rows, isLoadMore, id)
                } else {
                    view.onSetListDataCampaignRewardSuccess(mutableListOf(), isLoadMore, id)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onGetDataError(message)
            }
        })
    }
}