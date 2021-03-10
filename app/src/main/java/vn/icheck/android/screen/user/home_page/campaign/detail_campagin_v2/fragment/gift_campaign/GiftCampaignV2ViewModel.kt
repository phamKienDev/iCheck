package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.gift_campaign

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.campaign.CampainsInteractor
import vn.icheck.android.network.models.ICCampaign

class GiftCampaignV2ViewModel : ViewModel() {
    val campaignModel = CampainsInteractor()

    val onError = MutableLiveData<ICError>()
    val liveData = MutableLiveData<CampaignModel>()
    val iCoinSuccess = MutableLiveData<CampaignModel>()
    val moraleSuccess = MutableLiveData<CampaignModel>()

    fun checkInternet(id: String){
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        getCampaignRewardiCoin(id)
        getCampaignRewardMorale(id)
    }

    fun getCampaignRewardiCoin(id: String){
        campaignModel.getRewardiCoinCampaign(id, object : ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCampaign>>) {
                if (!obj.data?.rows.isNullOrEmpty()){
                    iCoinSuccess.postValue(CampaignModel(2, obj.data?.rows, null))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }

    fun getCampaignRewardMorale(id: String){
        campaignModel.getRewardiCoinCampaign(id, object : ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCampaign>>) {
                if (!obj.data?.rows.isNullOrEmpty()){
                    moraleSuccess.postValue(CampaignModel(3, obj.data?.rows, null))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }
}