package vn.icheck.android.loyalty.repository

import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.network.BaseRepository
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.network.ICNetworkClient
import vn.icheck.android.loyalty.network.SessionManager

internal class LoyaltyCustomersRepository : BaseRepository() {

    /**
     * Api Lấy danh sách các nhà mạng
     */
    fun getTopUpService(listener: ICApiListener<ICKResponse<TopupServiceResponse>>) {
        val url = APIConstants.LOYALTY_HOST + "public/loyalty/campaign/topup/service"
        requestApi(ICNetworkClient.getApiClientLoyalty().getTopUpService(url), listener)
    }

    /**
     * Api Lấy danh sách quà đã đổi của người dùng tích điểm dài hạn
     */
    fun getRedemptionHistoryLongTime(offset: Int, id: Long, listener: ICApiListener<ICKResponse<ICKListResponse<ICKRedemptionHistory>>>) {
        val params = hashMapOf<String, Any>()
        params["business_id"] = id
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val url = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/accumulate-member/history-exchange-gift"
        requestApi(ICNetworkClient.getApiClientLoyalty().getRedemptionHistoryLongTime(url, params), listener)
    }

    /**
     * Api Đổi quà tích điểm dài hạn
     */
    fun exchangeGift(businessGiftId: Long, receiver_phone: String?, serviceId: Long?, listener: ICApiListener<ICKResponse<ICKRedemptionHistory>>) {
        val user = SessionManager.session.user

        val params = hashMapOf<String, Any>()

        params["business_loyalty_gift_id"] = businessGiftId

        if (!receiver_phone.isNullOrEmpty()){
            params["receiver_phone"] = receiver_phone
        }

        if (serviceId != null){
            params["serviceId"] = serviceId
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

        val url = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/accumulate-member/exchange/gift"
        requestApi(ICNetworkClient.getApiClientLoyalty().exchangeGift(url, params), listener)
    }

    /**
     * Api Lấy danh sách đầu điểm
     */
    fun getLongTermProgramList(offset: Int, listener: ICApiListener<ICKResponse<ICKListResponse<ICKLongTermProgram>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val url = APIConstants.LOYALTY_HOST + "loyalty/loyalty/joined-network"
        requestApi(ICNetworkClient.getApiClientLoyalty().getLongTermProgramList(url, params), listener)
    }

    /**
     * Api Lấy chi tiết đầu điểm
     */
    fun getHeaderHomePage(id: Long, listener: ICApiListener<ICKResponse<ICKLongTermProgram>>) {
        val url = APIConstants.LOYALTY_HOST + "loyalty/loyalty/network/{id}/information".replace("{id}", "$id")
        requestApi(ICNetworkClient.getApiClientLoyalty().getHeaderHomePage(url), listener)
    }

    /**
     * Api lấy lịch sử giao dịch điểm
     */
    fun getTransactionHistory(id: Long, offset: Int, limit: Int, listener: ICApiListener<ICKResponse<ICKListResponse<ICKTransactionHistory>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = limit

        val url = APIConstants.LOYALTY_HOST + "loyalty/loyalty/joined-network/{id}/transaction-history".replace("{id}", "$id")
        requestApi(ICNetworkClient.getApiClientLoyalty().getTransactionHistory(url, params), listener)
    }

    /**
     * Api Lấy danh sách chương trình tích điểm dài hạn
     */
    fun getCampaignOfBusiness(id: Long, offset: Int, listener: ICApiListener<ICKResponse<ICKListResponse<ICKCampaignOfBusiness>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val url = APIConstants.LOYALTY_HOST + "loyalty/loyalty/joined-network/{id}/campaign".replace("{id}", "$id")
        requestApi(ICNetworkClient.getApiClientLoyalty().getCampaignOfBusiness(url, params), listener)
    }

    /**
     * Api Chi tiết chương trình tích điểm dài hạn
     */
    fun getCampaignDetailLongTime(id: Long, listener: ICApiListener<ICKResponse<ICKCampaignOfBusiness>>) {
        val url = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/$id"

        requestApi(ICNetworkClient.getApiClientLoyalty().getCampaignDetailLongTime(url), listener)
    }

    /**
     * Api Lấy lịch sử giao dịch điểm trong chương trình tích điểm dài hạn
     */
    fun getAccumulationHistory(id: Long, offset: Int, listener: ICApiListener<ICKResponse<ICKListResponse<ICKPointHistory>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        val url = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/{id}/history-get-points".replace("{id}", id.toString())
        requestApi(ICNetworkClient.getApiClientLoyalty().getAccumulationHistory(url, params), listener)
    }

    /**
     * Api Lấy chi tiết quà đã đổi tích điểm dài hạn
     */
    fun getDetailGift(id: Long, listener: ICApiListener<ICKResponse<ICKRedemptionHistory>>) {
        val url = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/accumulate-member/history-exchange-gift/{id}".replace("{id}", "$id")
        requestApi(ICNetworkClient.getApiClientLoyalty().getDetailGift(url), listener)
    }

    /**
     * Api Lấy chi tiết quà trong cửa hàng tích điểm dài hạn
     */
    fun getDetailGiftStore(id: Long, listener: ICApiListener<ICKResponse<ICKRedemptionHistory>>) {
        val url = APIConstants.LOYALTY_HOST + "loyalty/loyalty/network/gifts/{id}".replace("{id}", "$id")
        requestApi(ICNetworkClient.getApiClientLoyalty().getDetailGiftStoreLongTime(url), listener)
    }

    /**
     * Api Đổi quà PRODUCT tích điểm dài hạn
     */
    fun exchangeGift(businessGiftId: Long, name: String, phone: String, email: String?, cityId: Int, districtId: Int, address: String, city_name: String, district_name: String, wardId: Int, ward_name: String, listener: ICApiListener<ICKResponse<ICKRedemptionHistory>>) {

        val params = hashMapOf<String, Any>()

        params["business_loyalty_gift_id"] = businessGiftId

        params["district_id"] = districtId

        params["district_name"] = district_name

        params["phone"] = phone

        params["name"] = name

        params["city_id"] = cityId

        params["city_name"] = city_name

        params["ward_id"] = wardId

        params["ward_name"] = ward_name

        params["address"] = address

        if (!email.isNullOrEmpty()) {
            params["email"] = email
        }

        if (!SessionManager.session.user?.avatar.isNullOrEmpty()) {
            params["avatar"] = SessionManager.session.user?.avatar!!
        }

        val url = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/accumulate-member/exchange/gift"
        requestApi(ICNetworkClient.getApiClientLoyalty().exchangeGift(url, params), listener)
    }
}