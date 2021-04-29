package vn.icheck.android.network.feature.campaign

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import java.util.*

class ListCampaignInteractor : BaseInteractor() {

    fun getListCampaign(offset: Int, limit: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>>) {
        val fields = HashMap<String, Any>()
        fields["offset"] = offset
        fields["limit"] = limit
        requestNewApi(ICNetworkClient.getSocialApi().getListCampaign(fields), listener)
    }

    fun getSummary(listener: ICApiListener<ICSummary>, isDelay: Boolean = false) {
        if (!isDelay)
            requestApi(ICNetworkClient.getApiClient().summary, listener)
        else
            requestApiDelay(ICNetworkClient.getApiClient().summary, listener)
    }

    fun openShakeGift(idCampaign: String, count: Int, idBox: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICOpenShakeGift>>>) {
        val body = hashMapOf<String, Any>()
        body["id"] = idCampaign
        body["number"] = count
        body["icon_id"] = idBox
        requestNewApi(ICNetworkClient.getNewSocialApi().openShakeGift(body), listener)
    }

    fun getListIconCampaign(idCampaign: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICGridBoxShake>>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getListIconCampaign(idCampaign), listener)
    }

    fun getInfoCampaign(idCampaign: String, listener: ICNewApiListener<ICResponse<ICCampaign>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getInfoCampaign(idCampaign), listener)
    }

    fun getOnboarding(idCampaign: String, listener: ICNewApiListener<ICResponse<ICCampaignOnboarding>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getCampaignOnboarding(idCampaign), listener)
    }

    fun postOnboarding(idCampaign: String, listener: ICNewApiListener<ICResponse<Any>>) {
        val body= hashMapOf<String,Any>()
        body[""] = ""
        requestNewApi(ICNetworkClient.getSocialApi().postCampaignOnboarding(idCampaign,body), listener)
    }

    fun getTopWinnerCampaign(id: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getTopWinnerCampaign(id), listener)
    }

    fun getWinnerCampaign(id: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        requestNewApi(ICNetworkClient.getSocialApi().getWinnerCampaign(id, params), listener)
    }
}