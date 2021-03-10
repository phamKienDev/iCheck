package vn.icheck.android.loyalty.repository

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.network.BaseRepository
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.network.ICNetworkClient
import vn.icheck.android.loyalty.network.SessionManager

internal class VQMMRepository : BaseRepository() {

    fun getListGameLoyalty(offset: Int, listener: ICApiListener<ICKResponse<ICKListResponse<ICKGame>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/active/list"
        requestApi(ICNetworkClient.getApiClientLoyalty().getListGameLoyalty(host, params), listener)
    }

    suspend fun getListGame(): GameListRep? {
        return try {
            val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/list/game"
            ICNetworkClient.getApiClientLoyalty().getListGame(host)
        } catch (e: Exception) {
            null
        }
    }

    val mException = MutableLiveData<Exception>()

    suspend fun getGameInfoRep(campaignId: Long): LuckyWheelInfoRep? {
        return try {
            val host2 = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/$campaignId/game/lucky-wheel"
            ICNetworkClient.getApiClientLoyalty().getGameDetail(host2)
        } catch (e: Exception) {
            mException.postValue(e)
            null
        }
    }

    suspend fun customerPlayGame(campaignId: Long): PlayGameResp? {
        return try {
            val user = SessionManager.session.user
            val body = hashMapOf<String, Any?>()
            body["campaign_id"] = campaignId
            body["name"] = user?.name
            body["phone"] = user?.phone
            body["district_id"] = user?.district_id
            body["district_name"] = user?.district?.name
            body["city_id"] = user?.city_id
            body["city_name"] = user?.city?.name
            body["ward_id"] = user?.ward_id
            body["ward_name"] = user?.ward?.name
            body["avatar"] = user?.avatar
            body["email"] = user?.email
            body["address"] = user?.address

            val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/game/play"
            ICNetworkClient.getApiClientLoyalty().customerPlayGame(host, body)
        } catch (e: Exception) {
            mException.postValue(e)
            null
        }
    }

    suspend fun getListWinner(campaignId: Long, limit: Int, offset: Int): ListWinnerResp? {
        return try {
            val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/$campaignId/winners"
            ICNetworkClient.getApiClientLoyalty().getListWinner(host, limit, offset)
        } catch (e: Exception) {
            mException.postValue(e)
            null
        }
    }

    suspend fun getGame(id: Long, code: String): ReceiveGameResp? {
        return try {
            val user = SessionManager.session.user
            val requestBody = hashMapOf<String, Any?>()
            requestBody["campaign_id"] = id
            requestBody["code"] = code
            requestBody["name"] = user?.name
            requestBody["phone"] = user?.phone
            requestBody["district_id"] = user?.district_id
            requestBody["district_name"] = user?.district?.name
            requestBody["city_id"] = user?.city_id
            requestBody["city_name"] = user?.city?.name
            requestBody["ward_id"] = user?.ward_id
            requestBody["ward_name"] = user?.ward?.name
            requestBody["avatar"] = user?.avatar
            requestBody["email"] = user?.email
            requestBody["address"] = user?.address

            val host2 = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/game"
            ICNetworkClient.getApiClientLoyalty().receiveGame(host2, requestBody)
        } catch (e: Exception) {
            mException.postValue(e)
            null
        }
    }

    fun getGamePlay(campaignId: Long, target: String, listener: ICApiListener<ReceiveGameResp>) {
        val user = SessionManager.session.user

        val params = hashMapOf<String, Any>()

        params["campaign_id"] = campaignId

        params["target"] = target

        params["name"] = user?.name ?: ""
        params["phone"] = user?.phone ?: ""
        if (user?.district_id != null) {
            params["district_id"] = user.district_id!!
        }
        params["district_name"] = user?.district?.name ?: ""
        if (user?.city_id != null) {
            params["city_id"] = user.city_id!!
        }
        params["city_name"] = user?.city?.name ?: ""
        if (user?.ward_id != null) {
            params["ward_id"] = user.ward_id!!
        }
        params["ward_name"] = user?.ward?.name ?: ""
        params["avatar"] = user?.avatar ?: ""
        params["email"] = user?.email ?: ""
        params["address"] = user?.address ?: ""

        requestApi(ICNetworkClient.getApiClientLoyalty().receiveGameV2("loyalty/customer/campaign/game", params), listener)
    }

    fun getTopTheWinnerLoyalty(id: Long, listener: ICApiListener<ICKResponse<ICKListResponse<ICKCampaign>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 3
        params["campaign_id"] = id

        val host = APIConstants.LOYALTY_HOST + "public/loyalty/winner/top_winner"
        requestApi(ICNetworkClient.getApiClientLoyalty().getTheWinnerLoyalty(host, params), listener)
    }

    fun getTheWinnerLoyalty(id: Long, offset: Int, listener: ICApiListener<ICKResponse<ICKListResponse<ICKCampaign>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/$id/winners"
        requestApi(ICNetworkClient.getApiClientLoyalty().getTheWinnerLoyalty(host, params), listener)
    }

    fun getCodeUsed(id: Long, offset: Int, listener: ICApiListener<ICKResponse<ICKListResponse<ICKItemReward>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        params["campaign_id"] = id

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/code"
        requestApi(ICNetworkClient.getApiClientLoyalty().getListCodeUsed(host, params), listener)
    }

    fun getScanCodeUsed(id: Long, offset: Int, listener: ICApiListener<ICKResponse<ICKListResponse<ICKItemReward>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        params["campaign_id"] = id

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/history-scan"
        requestApi(ICNetworkClient.getApiClientLoyalty().getListCodeUsed(host, params), listener)
    }

    fun getListOfGiftsReceived(id: Long, offset: Int, listener: ICApiListener<ICKResponse<ICKListResponse<ICKRewardGameVQMMLoyalty>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        params["campaign_id"] = id

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/gifts"
        requestApi(ICNetworkClient.getApiClientLoyalty().getListOfGiftsReceived(host, params), listener)
    }
}