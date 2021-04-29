package vn.icheck.android.screen.user.winner_campaign

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.screen.user.listdistributor.BaseModelList

class WinnerCampaignViewModel : ViewModel() {
    private val interactor = ListCampaignInteractor()
    var campaignId: String? = null
    var offset = 0

    val onError = MutableLiveData<ICError>()
    val setListWinnerData = MutableLiveData<BaseModelList<CampaignModel>>()
    val topWinnerData = MutableLiveData<CampaignModel>()

    fun getData(intent: Intent) {
        campaignId = intent.getStringExtra(Constant.DATA_1)

        if (campaignId != null) {
            getTopWinner()
        } else {
            onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
        }
    }

    fun getHeaderAlpha(totalScroll: Int, layoutHeader: Int): Float {
        return when {
            totalScroll > 0 -> {
                if (totalScroll <= layoutHeader) {
                    (1f / layoutHeader) * totalScroll
                } else {
                    1f
                }
            }
            totalScroll < 0 -> {
                if (totalScroll < -layoutHeader) {
                    (-1f / layoutHeader) * totalScroll
                } else {
                    0f
                }
            }
            else -> {
                0f
            }
        }
    }

    private fun getTopWinner() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        interactor.getTopWinnerCampaign(campaignId.toString(), object : ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCampaign>>) {
                topWinnerData.postValue(CampaignModel(3, obj.data?.rows ?: mutableListOf(), null))
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }

    fun getListWinner(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        interactor.getWinnerCampaign(campaignId.toString(), offset, object : ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCampaign>>) {
                offset += APIConstants.LIMIT

                val list = mutableListOf<CampaignModel>()

                for (item in obj.data?.rows ?: mutableListOf()) {
                    list.add(CampaignModel(4, null, item))
                }

                setListWinnerData.postValue(BaseModelList(isLoadMore, list, null, null))
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }
}