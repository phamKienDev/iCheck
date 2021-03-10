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

    fun getDetailCampaign(id: String, listener: ICApiListener<ICDetail_Campaign>) {
        requestApi(ICNetworkClient.getApiClient().getDetailCampaign(id), listener)
    }

    fun getDetailCampaignV2(id: String, listener: ICNewApiListener<ICResponse<ICDetail_Campaign>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getDetailCampaignV2(id), listener)
    }

    fun getRewardCampaign(id: String, offset: Int, limit: Int, listener: ICApiListener<ICListResponse<ICCampaign_Reward>>) {
        val fields = HashMap<String, Any>()
        fields["offset"] = offset
        fields["limit"] = limit
        requestApi(ICNetworkClient.getApiClient().getListRewardCampaign(id, fields), listener)
    }

    fun getListUserRewardCampaign(id: String, page: Int, listener: ICApiListener<ICListResponse<ICCampaign_User_Reward>>) {
        val fields = HashMap<String, Any>()
        fields["offset"] = page
        fields["limit"] = APIConstants.LIMIT
        requestApi(ICNetworkClient.getApiClient().getListUserRewardCampaign(id, fields), listener)
    }

    fun joinCampaign(mId: String, listener: ICApiListener<ICJoinCampaign>) {
        val body = hashMapOf<String, Any>()
        body["id"] = mId
        requestApi(ICNetworkClient.getApiClient().putJoinCampaign(body), listener)
    }

    fun getListBoxReward(page: Int, pagesize: Int, listener: ICApiListener<ICListResponse<ICBoxReward>>) {
        val fields = HashMap<String, Any>()
        fields["offset"] = page
        fields["limit"] = pagesize
        requestApi(ICNetworkClient.getApiClient().getListBoxReward(fields), listener)
    }

    fun getListItemReward(page: Int, listener: ICApiListener<ICListResponse<ICItemReward>>) {
        val fields = HashMap<String, Any>()
        fields["offset"] = page
        fields["limit"] = APIConstants.LIMIT
        requestApi(ICNetworkClient.getApiClient().getListItemReward(fields), listener)
    }

    fun getDetailGift(idGift: String?, listener: ICApiListener<ICDetailGift>) {
        requestApi(ICNetworkClient.getApiClient().getDetailGift(idGift), listener)
    }


    fun unboxGift(mId: String, numberUnboxIcheck: Int, listener: ICApiListener<ICListResponse<ICUnBox_Gift>>) {
        val body = hashMapOf<String, Any>()
        body["id"] = mId
        body["number"] = numberUnboxIcheck.toString()
        requestApi(ICNetworkClient.getApiClient().unboxGift(body), listener)
    }

    fun getSummary(listener: ICApiListener<ICSummary>, isDelay: Boolean = false) {
        if (!isDelay)
            requestApi(ICNetworkClient.getApiClient().summary, listener)
        else
            requestApiDelay(ICNetworkClient.getApiClient().summary, listener)
    }

    fun onAcceptGift(giftID: String?, addressID: Long?, note: String?, listener: ICApiListener<ICAcceptGift>) {
        val body = hashMapOf<String, Any>()
        if (giftID != null) {
            body["id"] = giftID
        }
        if (addressID != null) {
            body["shipping_address_id"] = addressID
        }
        if (note != null) {
            body["note"] = note
        }
        requestApi(ICNetworkClient.getApiClient().acceptShipGift(body), listener)
    }

    fun onAcceptExchangeGiftStore(giftID: String?, addressID: Long?, note: String?, listener: ICApiListener<ICAcceptGift>) {
        val body = hashMapOf<String, Any>()
        if (giftID != null) {
            body["id"] = giftID
        }
        if (addressID != null) {
            body["shipping_address_id"] = addressID
        }
        if (!note.isNullOrEmpty()) {
            body["note"] = note
        }
        requestApi(ICNetworkClient.getApiClient().acceptExchangeGiftStore(body), listener)
    }

    fun onAcceptDaLayQua(idGift: String?, listener: ICApiListener<ICAcceptGift>) {
        val body = hashMapOf<String, Any>()
        if (idGift != null) {
            body["id"] = idGift
        }
        requestApi(ICNetworkClient.getApiClient().acceptDaLayQua(body), listener)
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