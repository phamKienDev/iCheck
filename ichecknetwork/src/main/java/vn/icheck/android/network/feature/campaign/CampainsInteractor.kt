package vn.icheck.android.network.feature.campaign

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.network.models.campaign.ICGiftOfCampaign

class CampainsInteractor : BaseInteractor() {

    fun getGiftHistory(campaignId: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICItemReward>>>){
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val url = APIConstants.socialHost + "social/api/loyalty/campaign/$campaignId/user-reward-his"
        requestNewApi(ICNetworkClient.getSocialApi().getGiftHistory(url, params), listener)
    }

    fun getListCampaign(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getCampainsHome(url), listener)
    }

    fun getRewardCampaign(id: String, rewardType: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICGiftOfCampaign>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 500
        params["rewardType"] = rewardType
        requestNewApi(ICNetworkClient.getSocialApi().getCampaignReward(id, params), listener)
    }

    fun getRewardiCoinCampaign(id: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getCampaignRewardiCoin(id), listener)
    }

    fun getTheWinnerCampaign(id: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        requestNewApi(ICNetworkClient.getSocialApi().getWinnerCampaign(id, params), listener)
    }

    fun getListRewardItem(offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICItemReward>>>) {
        val params = hashMapOf<String, Any>()
        // 2 sản phẩm
        // 3 thẻ cào
//        params["rewardType"] = 2
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
//        params["icheckId"] = "101957"

        requestNewApi(ICNetworkClient.getSocialApi().getRewardItemV2(params), listener)
    }

    fun getListMyGiftBox(offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICItemReward>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        requestNewApi(ICNetworkClient.getSocialApi().getListMyGiftBox(params), listener)
    }

    fun getDetailReward(id: String, listener: ICNewApiListener<ICResponse<ICItemReward>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getDetailReward(id), listener)
    }

    fun refuseGift(id: String, listId: MutableList<Int>, listMessage: MutableList<String>, listener: ICNewApiListener<ICResponseCode>) {
        val body = hashMapOf<String, Any>()
        body["id"] = id
        body["reasonCode"] = listId
        body["reason"] = listMessage
        requestNewApi(ICNetworkClient.getSocialApi().refuseGift(body), listener)
    }
}