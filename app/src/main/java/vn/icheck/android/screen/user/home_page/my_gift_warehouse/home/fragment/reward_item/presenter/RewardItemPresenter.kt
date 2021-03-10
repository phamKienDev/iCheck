package vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_item.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_item.view.IRewardItemView

class RewardItemPresenter (val view: IRewardItemView): BaseFragmentPresenter(view) {
    private var interactor = ListCampaignInteractor()
    private var offset = 0

    fun getDataItemReward(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (!isLoadMore)
            offset = 0

        interactor.getListItemReward(offset,object : ICApiListener<ICListResponse<ICItemReward>> {
            override fun onSuccess(obj: ICListResponse<ICItemReward>) {
                if (obj?.rows != null && !obj.rows.isNullOrEmpty()) {
                    offset += APIConstants.LIMIT
                    view.onSetListDataSuccess(obj.rows,obj.count, isLoadMore)
                }else{
                    view.onSetListDataSuccess(mutableListOf(),obj.count,isLoadMore)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetDataError(getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai))
            }
        })

    }
}