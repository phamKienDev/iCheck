package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.presenter

import android.os.Bundle
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.network.models.ICJoinCampaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.view.IInforCampaignView

class InforCampaignPresenter (val view: IInforCampaignView): BaseFragmentPresenter(view) {

    private var interactor = ListCampaignInteractor()

    fun getDataObject(arguments: Bundle?) {
        val obj = try {
            arguments?.getSerializable(Constant.DATA_1) as ICDetail_Campaign
        } catch (e: Exception) {
            null
        }
        view.getDataSuccess(obj!!)
    }

    fun joinCampaign(mId: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (mId.isNullOrEmpty()){
            view.onGetDataError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        interactor.joinCampaign(mId,object : ICApiListener<ICJoinCampaign> {
            override fun onSuccess(obj: ICJoinCampaign) {
                view.closeLoading()
                if (obj.code == 1){
                    view.onJoinCampaignSuccess(getString(R.string.tham_gia_thanh_cong))
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.closeLoading()
                view.onGetDataError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

}