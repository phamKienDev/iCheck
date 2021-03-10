package vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_box.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICBoxReward
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_box.view.IRewardBoxView

class RewardBoxPresenter(val view: IRewardBoxView) : BaseFragmentPresenter(view) {
    private var interactor = ListCampaignInteractor()
    private var offset = 0

    fun getDataBoxReward(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (!isLoadMore)
            offset = 0

        interactor.getListBoxReward(offset, Constant.PAGESIZE, object : ICApiListener<ICListResponse<ICBoxReward>> {
            override fun onSuccess(obj: ICListResponse<ICBoxReward>) {
                offset += APIConstants.LIMIT
                view.onSetListDataSuccess(obj.rows, obj.count, isLoadMore)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetDataError(getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai))
            }
        })

    }

}