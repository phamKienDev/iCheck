package vn.icheck.android.icheckscanditv6

import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.point_user.*

internal class RedeemPointRepository : BaseRepository() {

    fun getPointUser(id: Long, listener: ICApiListener<ICResponse<ICKPointUser>>) {
        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/${id}/accumulate/info"
        requestApi(ICNetworkClient.getApiClientLoyalty().getPointUser(host), listener)
    }

    fun getListRedemptionHistory(id: Long, offset: Int, listener: ICApiListener<ICResponse<ICListResponse<ICKBoxGifts>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/${id}/accumulate/gifts"
        requestApi(ICNetworkClient.getApiClientLoyalty().getListRedemptionHistory(host, params), listener)
    }

    fun postAccumulatePoint(campaignId: Long, code: String?, target: String?, listener: ICApiListener<ICResponse<ICKAccumulatePoint>>) {
        val params = hashMapOf<String, Any>()

        val user = SessionManager.session.user

        params["campaign_id"] = campaignId
        if (!code.isNullOrEmpty()) {
            params["code"] = code
        }
        if (!target.isNullOrEmpty()) {
            params["target"] = target
        }
        if (!user?.name.isNullOrEmpty()) {
            params["name"] = user?.name!!
        }
        if (!user?.phone.isNullOrEmpty()) {
            params["phone"] = user?.phone!!
        }
        if (!user?.email.isNullOrEmpty()) {
            params["email"] = user?.email!!
        }
        if (user?.city_id != null) {
            params["city_id"] = user.city_id!!
        }
        if (user?.district_id != null) {
            params["district_id"] = user.district_id!!
        }
        if (user?.ward_id != null) {
            params["ward_id"] = user.ward_id!!
        }
        if (!user?.address.isNullOrEmpty()) {
            params["address"] = user?.address!!
        }
        if (!user?.city?.name.isNullOrEmpty()) {
            params["city_name"] = user?.city?.name!!
        }
        if (!user?.district?.name.isNullOrEmpty()) {
            params["district_name"] = user?.district?.name!!
        }
        if (!user?.ward?.name.isNullOrEmpty()) {
            params["ward_name"] = user?.ward?.name!!
        }
        if (!user?.avatar.isNullOrEmpty()) {
            params["avatar"] = user?.avatar!!
        }

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/accumulate-point"
        requestApi(ICNetworkClient.getApiClientLoyalty().postNhapMaTichDiem(host, params), listener)
    }


    fun exchangeCardGiftTDNH(campaignId: Long, giftID: Long, serviceId: Long, receiverPhone: String, listener: ICApiListener<ICResponse<ICKBoxGifts>>) {
        val params = hashMapOf<String, Any>()

        val user = SessionManager.session.user

        params["campaign_id"] = campaignId
        params["gift_id"] = giftID
        params["serviceId"] = serviceId
        params["receiver_phone"] = receiverPhone

        if (!user?.name.isNullOrEmpty()) {
            params["name"] = user?.name!!
        }
        if (!user?.phone.isNullOrEmpty()) {
            params["phone"] = user?.phone!!
        }
        if (!user?.email.isNullOrEmpty()) {
            params["email"] = user?.email!!
        }
        if (user?.city_id != null) {
            params["city_id"] = user.city_id!!
        }
        if (user?.district_id != null) {
            params["district_id"] = user.district_id!!
        }
        if (user?.ward_id != null) {
            params["ward_id"] = user.ward_id!!
        }
        if (!user?.address.isNullOrEmpty()) {
            params["address"] = user?.address!!
        }
        if (!user?.city?.name.isNullOrEmpty()) {
            params["city_name"] = user?.city?.name!!
        }
        if (!user?.district?.name.isNullOrEmpty()) {
            params["district_name"] = user?.district?.name!!
        }
        if (!user?.ward?.name.isNullOrEmpty()) {
            params["ward_name"] = user?.ward?.name!!
        }
        if (!user?.avatar.isNullOrEmpty()) {
            params["avatar"] = user?.avatar!!
        }

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/exchange/gift"
        requestApi(ICNetworkClient.getApiClientLoyalty().postExchangeGift(host, params), listener)
    }

    /**
     * Api Lấy danh sách quà đã đổi của người dùng tích điểm đổi quà
     */
    fun getListOfGiftsReceived(id: Long, offset: Int, listener: ICApiListener<ICResponse<ICListResponse<ICKRewardGameLoyalty>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        params["campaign_id"] = id

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/gifts"
        requestApi(ICNetworkClient.getApiClientLoyalty().getListOfGiftsReceivedLoyalty(host, params), listener)
    }

    fun getTopWinnerPoint(campaignId: Long, listener: ICApiListener<ICResponse<ICListResponse<ICKPointUser>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 3

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/${campaignId}/top-accumulate-points"
        requestApi(ICNetworkClient.getApiClientLoyalty().getWinnerPoint(host, params), listener)
    }

    fun getTheWinnerPoint(campaignId: Long, offset: Int, listener: ICApiListener<ICResponse<ICListResponse<ICKPointUser>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/${campaignId}/recent-accumulate-points"
        requestApi(ICNetworkClient.getApiClientLoyalty().getWinnerPoint(host, params), listener)
    }

    fun postExchangeGift(
            campaignId: Long,
            giftID: Long,
            name: String?,
            phone: String?,
            email: String?,
            cityId: Int?,
            districtId: Int?,
            address: String?,
            city_name: String?,
            district_name: String?,
            wardId: Int?,
            ward_name: String?,
            listener: ICApiListener<ICResponse<ICKBoxGifts>>) {
        val params = hashMapOf<String, Any>()
        params["campaign_id"] = campaignId

        params["gift_id"] = giftID

        if (districtId != null) {
            params["district_id"] = districtId
        }

        if (!district_name.isNullOrEmpty()) {
            params["district_name"] = district_name
        }

        if (!phone.isNullOrEmpty()) {
            params["phone"] = phone
        }

        if (!name.isNullOrEmpty()) {
            params["name"] = name
        }

        if (cityId != null) {
            params["city_id"] = cityId
        }

        if (!city_name.isNullOrEmpty()) {
            params["city_name"] = city_name
        }

        if (wardId != null) {
            params["ward_id"] = wardId
        }

        if (!ward_name.isNullOrEmpty()) {
            params["ward_name"] = ward_name
        }

        if (!email.isNullOrEmpty()) {
            params["email"] = email
        }

        if (!SessionManager.session.user?.avatar.isNullOrEmpty()) {
            params["avatar"] = SessionManager.session.user?.avatar!!
        }

        if (!address.isNullOrEmpty()) {
            params["address"] = address
        }

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/exchange/gift"
        requestApi(ICNetworkClient.getApiClientLoyalty().postExchangeGift(host, params), listener)
    }


    fun getPointHistoryAll(campaignId: Long, offset: Int, target: String, type: String?, listener: ICApiListener<ICResponse<ICListResponse<ICKPointHistory>>>) {
        val params = hashMapOf<String, Any>()
        params["target"] = target
        if (!type.isNullOrEmpty()) {
            params["type"] = type
        }
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/${campaignId}/history-get-points"
        requestApi(ICNetworkClient.getApiClientLoyalty().getPointHistoryAll(host, params), listener)
    }
}