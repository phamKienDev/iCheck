package vn.icheck.android.screen.user.campaign_onboarding

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.ResponseBody
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.network.models.ICCampaignOnboarding

class CampaignOnboardingViewModel : ViewModel() {
    private var interactor = ListCampaignInteractor()

    var onBoardingData = MutableLiveData<ICCampaignOnboarding>()
    var onState = MutableLiveData<ICMessageEvent>()

    var campaign: ICCampaign? = null
    var campaignId: String? = null


    fun getData(intent: Intent) {
        try {
            campaign = intent.getSerializableExtra(Constant.DATA_1) as ICCampaign
        } catch (e: Exception) {
        }

        try {
            campaignId = intent.getSerializableExtra(Constant.DATA_1) as String
        } catch (e: Exception) {
        }


        when {
            campaign != null -> {
                campaignId = campaign!!.id
                getOnboarding()
            }
            campaignId != null -> {
                getInfoCampaign()
            }
            else -> {
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR, ICError(R.drawable.ic_error_request, ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null)))
            }
        }
    }

    private fun getOnboarding() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET))
            return
        }

        interactor.getOnboarding(campaignId!!, object : ICNewApiListener<ICResponse<ICCampaignOnboarding>> {
            override fun onSuccess(obj: ICResponse<ICCampaignOnboarding>) {
                onBoardingData.postValue(obj.data)
                postOnboarding()
            }

            override fun onError(error: ICResponseCode?) {
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error!!.message
                }
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR, ICError(R.drawable.ic_error_request, message, null, null)))
            }
        })
    }

    private fun getInfoCampaign() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET))
            return
        }

        interactor.getInfoCampaign(campaignId!!, object : ICNewApiListener<ICResponse<ICCampaign>> {
            override fun onSuccess(obj: ICResponse<ICCampaign>) {
                campaign = obj.data
                getOnboarding()
            }

            override fun onError(error: ICResponseCode?) {
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error!!.message
                }
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR, ICError(R.drawable.ic_error_request, message, null, null)))
            }
        })
    }

    private fun postOnboarding() {
        interactor.postOnboarding(campaignId!!, object : ICNewApiListener<ICResponse<Any>> {
            override fun onSuccess(obj: ICResponse<Any>) {
            }

            override fun onError(error: ICResponseCode?) {
            }
        })
    }

}